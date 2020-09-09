package org.rrx.jcache.clients.test;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/4 11:50
 * @Description:
 */
public interface RedisClientInter {

    public String get(String key);

    public String set(String key, String value);
}
