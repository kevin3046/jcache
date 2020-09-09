package org.rrx.jcache.commons.config.properties;

import org.rrx.jcache.commons.config.spring.annotation.JcacheValue;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/28 15:15
 * @Description:
 */
public class JedisProperties {

    @JcacheValue(value = "jedis.pool.testOnBorrow")
    private boolean testOnBorrow;

    @JcacheValue(value = "jedis.pool.minIdle")
    private Integer minIdle;

    @JcacheValue(value = "jedis.pool.maxIdle")
    private Integer maxIdle;

    @JcacheValue(value = "jedis.pool.softMinEvictableIdleTime")
    private Integer softMinEvictableIdleTime;

    @JcacheValue(value = "jedis.pool.maxTotal")
    private Integer maxTotal;

    @JcacheValue(value = "jedis.pool.maxWaitMillis")
    private Integer maxWaitMillis;

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Integer getSoftMinEvictableIdleTime() {
        return softMinEvictableIdleTime;
    }

    public void setSoftMinEvictableIdleTime(Integer softMinEvictableIdleTime) {
        this.softMinEvictableIdleTime = softMinEvictableIdleTime;
    }

    public Integer getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Integer maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }
}
