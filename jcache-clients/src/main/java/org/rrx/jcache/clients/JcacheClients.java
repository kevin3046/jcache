package org.rrx.jcache.clients;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.rrx.jcache.clients.cache.HotsCache;
import org.rrx.jcache.clients.config.spring.annotation.Disposable;
import org.rrx.jcache.commons.config.properties.ClientBaseProperties;
import org.rrx.jcache.commons.config.properties.JcacheClientConfigProperties;
import org.rrx.jcache.commons.constants.CommonConstants;
import org.rrx.jcache.commons.disruptor.DisruptorFactory;
import org.rrx.jcache.commons.dto.AppConfigBean;
import org.rrx.jcache.commons.dto.CacheBean;
import org.rrx.jcache.commons.dto.MessageBean;
import org.rrx.jcache.commons.etcd.EtcdClients;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.commons.rocketmq.RocketProducer;
import org.rrx.setcd.commons.clients.GetOptionBuilder;
import org.rrx.setcd.commons.clients.KeyValueBean;
import org.rrx.setcd.commons.clients.SetcdClients;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 15:42
 * @Description:
 */
public class JcacheClients implements Disposable {

    private static final Logger log = LogFactory.getLogger(JcacheClients.class);

    private RocketProducer rocketProducer;

    private DisruptorFactory disruptorFactory;

    private JcacheClientConfigProperties configProperties;

    private ClientBaseProperties baseProperties;

    private HotsCache hotsCache;

    private static JcacheClients instance;

    private SetcdClients setcdClients;

    public JcacheClients(JcacheClientConfigProperties configProperties) {
        this.configProperties = configProperties;
        this.baseProperties = configProperties.getClientBaseProperties();
    }

    public static JcacheClients getInstance() {
        return instance;
    }

    public void initClients() {

        if (instance == null) {
            synchronized (JcacheClients.class) {
                if (instance == null) {
                    //初始化本地缓存池
                    this.hotsCache = new HotsCache();
                    //初始化mq生产者
                    this.rocketProducer = new RocketProducer(configProperties.getRocketmqProperties());
                    this.rocketProducer.initProducer();
                    //初始化disruptor
                    this.disruptorFactory = new DisruptorFactory(new SendMessageHandler(configProperties, rocketProducer));
                    this.disruptorFactory.start();
                    //初始化etcd连接
                    EtcdClients etcdClients = new EtcdClients(configProperties.getEtcdProperties());
                    this.setcdClients = etcdClients.setcdClientsBuild();
                    //注册app信息
                    registerAppconfig();
                    //预热缓存
                    loadCache();
                    //初始化etcd监听器
                    EtcdListener etcdListener = new EtcdListener(configProperties, setcdClients, hotsCache);
                    etcdListener.startHotKeyListener();
                    instance = this;
                    log.info("JcacheClients start success");
                }
            }
        }

    }

    @Override
    public void destroy() {
        setcdClients.close();
        rocketProducer.close();
        disruptorFactory.shutdown();
    }

    public String get(String key) {

        //先查询本地缓存
        CacheBean cacheBean = hotsCache.get(key);
        if (cacheBean != null) {
            //判断key的过期时间
            if (cacheBean.expireTime >= (System.currentTimeMillis() / 1000)) {
                log.debug(key + " hit cache");
                return cacheBean.value;
            } else {
                removeCache(key);
            }
        }
        return null;
    }

    public void getAllCache() {
        Map<String, HotsCache.DLinkedNode> map = hotsCache.getCache();
        for (String key : map.keySet()) {
            log.debug(key + "==" + map.get(key).cacheBean.toString());
        }
    }

    public void set(String key, String value) {
        removeCache(key);
    }

    public void expire(String key, int expire) {
        CacheBean cacheBean = hotsCache.get(key);
        if (cacheBean != null) {
            cacheBean.expireTime = System.currentTimeMillis() / 1000 + expire;
        }
        removeCache(key);
    }

    public void del(String key) {
        removeCache(key);
    }

    public void removeCache(String key) {
        try {
            CacheBean cacheBean = hotsCache.get(key);
            if (cacheBean != null) {
                //失效本地的key
                hotsCache.remove(key);
                //广播失效事件
                setcdClients.delValue(CommonConstants.getHotsDirKey(baseProperties.getAppname(), key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void sendMessage(String key) {

        MessageBean messageBean = new MessageBean();
        messageBean.setAppName(baseProperties.getAppname());
        messageBean.setKey(key);
        messageBean.setSendTime((int) (System.currentTimeMillis() / 1000));
        String msg = JSON.toJSONString(messageBean, SerializerFeature.WriteMapNullValue);
        this.disruptorFactory.onData(msg);
    }

    private void registerAppconfig() {
        AppConfigBean appConfigBean = new AppConfigBean();
        appConfigBean.setAppname(baseProperties.getAppname());
        appConfigBean.setRedisPrefix(baseProperties.getRedisPrefix());
        appConfigBean.setHotsTopSize(baseProperties.getHotsTopSize());
        appConfigBean.setHotsThreshold(baseProperties.getHotsThreshold());

        String configDir = CommonConstants.ETCD_CONFIG + CommonConstants.ETCD_CONFIG_APP + "/" + baseProperties.getAppname();
        String appConfig = JSON.toJSONString(appConfigBean, SerializerFeature.WriteMapNullValue);
        try {
            this.setcdClients.putValue(configDir, appConfig);
            log.info("registerAppconfig success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("registerAppconfig Exception:{}", e);
        }
    }

    private void loadCache() {
        String dir = CommonConstants.getHotsDir(baseProperties.getAppname());
        try {
            List<KeyValueBean> ret = this.setcdClients.getValue(dir, new GetOptionBuilder().withPrefix(dir));
            if (CollectionUtils.isEmpty(ret)) {
                return;
            }
            ret.forEach(keyValueBean -> {
                CacheBean cacheBean = JSON.parseObject(keyValueBean.getValue(), CacheBean.class);
                if (cacheBean != null && cacheBean.expireTime > (System.currentTimeMillis() / 1000)) {
                    String key = keyValueBean.getKey().substring(dir.length() + 1, keyValueBean.getKey().length());
                    hotsCache.put(key, cacheBean);
                }
            });
            log.debug("loadCache success size is:" + hotsCache.getCache().size());
        } catch (Exception e) {
            log.error("loadCache Exception:{}", e);
        }
    }

}
