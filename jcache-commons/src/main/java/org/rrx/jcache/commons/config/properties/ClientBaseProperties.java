package org.rrx.jcache.commons.config.properties;

import org.rrx.jcache.commons.config.spring.annotation.JcacheValue;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/8 19:46
 * @Description:
 */
public class ClientBaseProperties {

    //应用名称
    @JcacheValue(value = "base.appname")
    private String appname;

    //所使用的redis前缀
    @JcacheValue(value = "base.redisPrefix")
    private String redisPrefix;

    //要维持的热点key的最大个数
    @JcacheValue(value = "base.hotsTopSize")
    private Integer hotsTopSize;

    //热点key阀值,所以key共享
    @JcacheValue(value = "base.hotsThreshold")
    private Integer hotsThreshold;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getRedisPrefix() {
        return redisPrefix;
    }

    public void setRedisPrefix(String redisPrefix) {
        this.redisPrefix = redisPrefix;
    }

    public Integer getHotsTopSize() {
        return hotsTopSize;
    }

    public void setHotsTopSize(Integer hotsTopSize) {
        this.hotsTopSize = hotsTopSize;
    }

    public Integer getHotsThreshold() {
        return hotsThreshold;
    }

    public void setHotsThreshold(Integer hotsThreshold) {
        this.hotsThreshold = hotsThreshold;
    }
}
