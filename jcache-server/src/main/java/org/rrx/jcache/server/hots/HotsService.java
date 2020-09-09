package org.rrx.jcache.server.hots;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections.MapUtils;
import org.rrx.jcache.commons.constants.CommonConstants;
import org.rrx.jcache.commons.dto.AppConfigBean;
import org.rrx.jcache.commons.dto.CacheBean;
import org.rrx.jcache.commons.dto.MessageBean;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.commons.redis.RedisClients;
import org.rrx.setcd.commons.clients.GetOptionBuilder;
import org.rrx.setcd.commons.clients.KeyValueBean;
import org.rrx.setcd.commons.clients.SetcdClients;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/27 14:16
 * @Description:热度处理服务
 */
public class HotsService {

    private static final Logger log = LogFactory.getLogger(HotsService.class);

    private ConcurrentHashMap<String, ConcurrentHashMap<String, LongWheel>> hotsCache = new ConcurrentHashMap<>();

    private RedisClients redisClients;

    private SetcdClients setcdClients;

    private AtomicLong localTicks = new AtomicLong(0);


    public HotsService(RedisClients redisClients, SetcdClients setcdClients) {
        this.redisClients = redisClients;
        this.setcdClients = setcdClients;
    }

    public LongWheel getHotsValue(String appName, String key) {
        ConcurrentHashMap map = hotsCache.get(appName);
        if (map == null) {
            hotsCache.putIfAbsent(appName, new ConcurrentHashMap<String, LongWheel>());
        }
        LongWheel longWheel = hotsCache.get(appName).get(key);
        if (longWheel == null) {
            hotsCache.get(appName).putIfAbsent(key, new LongWheel());
        }
        return hotsCache.get(appName).get(key);
    }

    /**
     * 访问上报累加
     *
     * @param messageBean
     */
    public void reportHots(MessageBean messageBean) {
        getHotsValue(messageBean.getAppName(), messageBean.getKey()).counts.incrementAndGet();
    }

    /**
     * 热度汇聚
     */
    public void hotsConverge() {

        if (MapUtils.isEmpty(hotsCache)) {
            return;
        }
        //本地热度汇聚,映射到环形队列
        hotsCache.keySet().forEach(this::localConvergeByAppname);
        //本地汇聚结果,同步到redis
        if (!setLockTicks()) {
            return;
        }
        hotsCache.keySet().forEach(this::redisConvergeByAppname);
    }

    /**
     * 获取redis保存的当前序号轮数
     *
     * @return
     */
    private boolean setLockTicks() {

        String result = this.redisClients.get(CommonConstants.REDIS_HOTS_TICKS);
        if (StringUtils.isEmpty(result)) {
            log.debug("REDIS_HOTS_TICKS isEmpty");
            return false;
        }
        Long ticks = Long.valueOf(result);
        //如果当前轮数,等于上一次的轮数,则忽略,不进行轮数汇聚
        if (ticks.equals(this.localTicks.longValue())) {
            return false;
        }
        //设置
        localTicks.set(ticks);
        return true;
    }


    /**
     * 本地汇聚by appname
     *
     * @param appname
     */
    private void localConvergeByAppname(String appname) {

        try {
            //遍历map,把当前热度,添加到对应的时间轮中
            ConcurrentHashMap<String, LongWheel> map = hotsCache.get(appname);
            map.keySet().forEach(key -> {
                LongWheel longWheel = map.get(key);
                longWheel.add(longWheel.counts.getAndSet(0L));
            });
        } catch (Exception e) {
            log.error("localConvergeByAppname Exception:{}", e);
        }
    }

    /**
     * redis汇聚by appname
     *
     * @param appname
     */
    private void redisConvergeByAppname(String appname) {

        try {
            ConcurrentHashMap<String, LongWheel> map = hotsCache.get(appname);
            for (String key : map.keySet()) {
                LongWheel longWheel = map.get(key);
                String hotsKey = CommonConstants.getRedisHotsKey(appname, localTicks.get());
                long sum = longWheel.sum();
                if (sum > 0) {
                    //空闲次数归0
                    longWheel.idleTimes.set(0);
                    this.redisClients.zincrBy(hotsKey, sum, key);
                } else {
                    //自增空闲次数
                    longWheel.idleTimes.incrementAndGet();
                    //当空闲次数超过阀值,则清理本地缓存
                    if (longWheel.isCanRemove()) {
                        hotsCache.get(appname).remove(key);
                    }
                }
            }
        } catch (Exception e) {
            log.error("redisConvergeByAppname Exception:{}", e);
        }
    }


    /**
     * 热点探测,仅由master节点执行
     */
    public void hotsDetect() {
        try {
            List<AppConfigBean> appConfigBeans = getAppConfigs();
            if (CollectionUtils.isEmpty(appConfigBeans)) {
                return;
            }
            //获取当前最新的轮数
            String result = this.redisClients.get(CommonConstants.REDIS_HOTS_TICKS);
            if (StringUtils.isEmpty(result)) {
                result = "0";
            }
            Long ticks = Long.valueOf(result);
            //热度探测
            appConfigBeans.forEach(appConfigBean -> detectByAppname(appConfigBean, ticks));
            //清理序号轮,只保持最近12轮的数据,1分钟
            appConfigBeans.forEach(appConfigBean -> clearTopList(appConfigBean.getAppname(), ticks));
            //自增轮数
            if (ticks >= Integer.MAX_VALUE) {
                redisClients.del(CommonConstants.REDIS_HOTS_TICKS);
                appConfigBeans.forEach(appConfigBean -> clearAllTopList(appConfigBean.getAppname(), ticks));
            } else {
                redisClients.incr(CommonConstants.REDIS_HOTS_TICKS);
            }
        } catch (Exception e) {
            log.error("hotsDetect Exception:{}", e);
        }
    }

    private void detectByAppname(AppConfigBean appConfigBean, Long ticks) {

        Set<Tuple> topList = getRedisTopList(appConfigBean, ticks);

        if (CollectionUtils.isEmpty(topList)) {
            return;
        }
        topList.forEach(tuple -> processTuple(appConfigBean, tuple));
    }

    private Set<Tuple> getRedisTopList(AppConfigBean appConfigBean, Long ticks) {
        String hotsKey = CommonConstants.getRedisHotsKey(appConfigBean.getAppname(), ticks);
        return redisClients.zrevrangeWithScores(hotsKey, 0, appConfigBean.getHotsTopSize());
    }

    private void processTuple(AppConfigBean appConfigBean, Tuple tuple) {

        try {
            String value = this.redisClients.getNoPrefix(appConfigBean.getRedisPrefix() + tuple.getElement());
            Long ttl = this.redisClients.ttlNoPrefix(appConfigBean.getRedisPrefix() + tuple.getElement());
            if (StringUtils.isEmpty(value) || ttl <= 0 || tuple.getScore() < appConfigBean.getHotsThreshold()) {
                return;
            }

            log.debug("member:" + tuple.getElement() + ",score:" + tuple.getScore() + ",value:" + value + ",ttl:" + ttl);

            CacheBean cacheBean = new CacheBean(value, (long) tuple.getScore(), ((System.currentTimeMillis() / 1000) + ttl));
            String hotsEtcdKey = CommonConstants.getHotsDirKey(appConfigBean.getAppname(), tuple.getElement());
            String msg = JSON.toJSONString(cacheBean, SerializerFeature.WriteMapNullValue);
            this.setcdClients.putValue(hotsEtcdKey, msg);

        } catch (Exception e) {
            log.error("detectByAppname Exception:{}", e);
        }
    }

    private void clearTopList(String appname, Long ticks) {
        if (ticks > 12) {
            this.redisClients.del(CommonConstants.getRedisHotsKey(appname, ticks - 12));
        }
    }

    private void clearAllTopList(String appname, Long ticks) {
        for (int i = 0; i <= 12; i++) {
            this.redisClients.del(CommonConstants.getRedisHotsKey(appname, ticks - i));
        }
    }

    /**
     * 获取所有应用保存在etcd的配置
     *
     * @return
     */
    private List<AppConfigBean> getAppConfigs() {

        List<AppConfigBean> result = new ArrayList<>();
        String key = CommonConstants.ETCD_CONFIG + CommonConstants.ETCD_CONFIG_APP;
        try {
            List<KeyValueBean> ret = this.setcdClients.getValue(key, new GetOptionBuilder().withPrefix(key));
            if (CollectionUtils.isEmpty(ret)) {
                return result;
            }
            ret.forEach(keyValueBean -> {
                AppConfigBean appConfigBean = JSON.parseObject(keyValueBean.getValue(), AppConfigBean.class);
                result.add(appConfigBean);
            });
            return result;
        } catch (Exception e) {
            log.error("getAppConfigs Exception:{}", e);
            return result;
        }
    }
}
