package org.rrx.jcache.commons.config.properties;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:29
 * @Description:
 */
public class JcacheServerConfigProperties {

    private String appname = "jcache_server";

    private EtcdProperties etcdProperties;

    private JedisProperties jedisProperties;

    private RedisProperties redisProperties;

    private RocketmqProperties rocketmqProperties;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public EtcdProperties getEtcdProperties() {
        return etcdProperties;
    }

    public void setEtcdProperties(EtcdProperties etcdProperties) {
        this.etcdProperties = etcdProperties;
    }

    public JedisProperties getJedisProperties() {
        return jedisProperties;
    }

    public void setJedisProperties(JedisProperties jedisProperties) {
        this.jedisProperties = jedisProperties;
    }

    public RedisProperties getRedisProperties() {
        return redisProperties;
    }

    public void setRedisProperties(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    public RocketmqProperties getRocketmqProperties() {
        return rocketmqProperties;
    }

    public void setRocketmqProperties(RocketmqProperties rocketmqProperties) {
        this.rocketmqProperties = rocketmqProperties;
    }
}
