package org.rrx.jcache.commons.etcd;

import org.rrx.jcache.commons.config.properties.EtcdProperties;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.setcd.commons.clients.SetcdClientBuilder;
import org.rrx.setcd.commons.clients.SetcdClients;
import org.slf4j.Logger;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/30 16:22
 * @Description:
 */
public class EtcdClients {

    private static final Logger log = LogFactory.getLogger(EtcdClients.class);

    private EtcdProperties etcdProperties;

    private SetcdClients setcdClients;

    public EtcdClients(EtcdProperties etcdProperties) {
        this.etcdProperties = etcdProperties;
    }


    public SetcdClients setcdClientsBuild() {
        this.setcdClients = SetcdClientBuilder.builder()
                .namespace(etcdProperties.getNamespace())
                .serverAddr(etcdProperties.getServerAddr())
                .build();
        log.info("EtcdClients start success");
        return this.setcdClients;
    }


}
