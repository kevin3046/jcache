package org.rrx.jcache.commons.config.properties;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:29
 * @Description:
 */
public class JcacheClientConfigProperties {

    private ClientBaseProperties clientBaseProperties;

    private EtcdProperties etcdProperties;

    private RocketmqProperties rocketmqProperties;

    public ClientBaseProperties getClientBaseProperties() {
        return clientBaseProperties;
    }

    public void setClientBaseProperties(ClientBaseProperties clientBaseProperties) {
        this.clientBaseProperties = clientBaseProperties;
    }

    public EtcdProperties getEtcdProperties() {
        return etcdProperties;
    }

    public void setEtcdProperties(EtcdProperties etcdProperties) {
        this.etcdProperties = etcdProperties;
    }

    public RocketmqProperties getRocketmqProperties() {
        return rocketmqProperties;
    }

    public void setRocketmqProperties(RocketmqProperties rocketmqProperties) {
        this.rocketmqProperties = rocketmqProperties;
    }
}
