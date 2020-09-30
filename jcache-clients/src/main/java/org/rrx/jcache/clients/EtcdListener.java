package org.rrx.jcache.clients;

import com.alibaba.fastjson.JSON;
import io.etcd.jetcd.watch.WatchEvent;
import org.rrx.jcache.clients.cache.LocalCache;
import org.rrx.jcache.commons.config.properties.JcacheClientConfigProperties;
import org.rrx.jcache.commons.constants.CommonConstants;
import org.rrx.jcache.commons.dto.CacheBean;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.setcd.commons.clients.SetcdClients;
import org.rrx.setcd.commons.event.AbstractNotifyListener;
import org.rrx.setcd.commons.event.SetcdEventBean;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/25 16:30
 * @Description:
 */
public class EtcdListener {

    private static final Logger log = LogFactory.getLogger(JcacheClients.class);

    private JcacheClientConfigProperties configProperties;

    private SetcdClients setcdClients;

    private LocalCache localCache;

    public EtcdListener(JcacheClientConfigProperties configProperties, SetcdClients setcdClients, LocalCache localCache) {
        this.configProperties = configProperties;
        this.setcdClients = setcdClients;
        this.localCache = localCache;
    }

    public void startHotKeyListener() {

        String dir = CommonConstants.getHotsDir(configProperties.getClientBaseProperties().getAppname());

        this.setcdClients.addListener(dir, new AbstractNotifyListener(dir, 3000) {
            @Override
            protected void onReceived(SetcdEventBean eventBean) {
                log.debug(eventBean.toString());
                try {
                    String redisKey = eventBean.getKey().substring(dir.length() + 1, eventBean.getKey().length());
                    if (eventBean.getEventType().equals(WatchEvent.EventType.PUT)) {
                        if (StringUtils.isEmpty(eventBean.getValue())) {
                            return;
                        }
                        CacheBean cacheBean = JSON.parseObject(eventBean.getValue(), CacheBean.class);
                        localCache.put(redisKey, cacheBean);
                    } else {
                        //任一客户端，发生set expire del 事件, 则同步失效本地的缓存
                        localCache.remove(redisKey);
                    }

                } catch (Exception e) {
                    log.error("startHotKeyListener.onReceived Exception:{}", e);
                }
            }
        }, true);
        log.info("EtcdListener startHotKeyListener success");
    }
}
