package org.rrx.jcache.commons.dto;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 17:13
 * @Description:
 */
public class CacheBean {

    public String value;

    public Long hots = 0L;//热度

    public Long expireTime = 2208960000L;

    public CacheBean(String value) {
        this.value = value;
    }

    public CacheBean(String value, Long hots) {
        this.value = value;
        this.hots = hots;
    }

    public CacheBean(String value, Long hots, Long expireTime) {
        this.value = value;
        this.hots = hots;
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "CacheBean{" +
                "value='" + value + '\'' +
                ", hots=" + hots +
                ", expireTime=" + expireTime +
                '}';
    }
}
