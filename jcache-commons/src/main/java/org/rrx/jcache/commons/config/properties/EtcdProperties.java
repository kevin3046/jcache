package org.rrx.jcache.commons.config.properties;

import org.rrx.jcache.commons.config.spring.annotation.JcacheValue;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/28 15:15
 * @Description:
 */
public class EtcdProperties {

    @JcacheValue(value = "etcd.serverAddr")
    private String serverAddr;

    @JcacheValue(value = "etcd.namespace")
    private String namespace;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
