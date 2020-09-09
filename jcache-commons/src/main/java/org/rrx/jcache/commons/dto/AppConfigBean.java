package org.rrx.jcache.commons.dto;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/2 10:25
 * @Description:
 */
public class AppConfigBean {

    private String appname;

    private String redisPrefix;

    private Integer hotsTopSize;

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
