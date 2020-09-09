package org.rrx.jcache.commons.config.properties;

import org.rrx.jcache.commons.config.spring.annotation.JcacheValue;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/28 15:15
 * @Description:
 */
public class RedisProperties {

    @JcacheValue(value = "redis.host")
    private String host;

    @JcacheValue(value = "redis.port")
    private Integer port;

    @JcacheValue(value = "redis.password")
    private String password;

    @JcacheValue(value = "redis.database")
    private Integer database = 0;

    @JcacheValue(value = "redis.prefix")
    private String prefix;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


}
