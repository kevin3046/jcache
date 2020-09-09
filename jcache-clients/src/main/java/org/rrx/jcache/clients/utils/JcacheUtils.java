package org.rrx.jcache.clients.utils;


import org.rrx.jcache.clients.JcacheClients;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 16:15
 * @Description:
 */
public class JcacheUtils {


    public static void set(String key, String value) {
        JcacheClients.getInstance().set(key, value);
    }

    public static void expire(String key, int expire) {
        JcacheClients.getInstance().expire(key, expire);
    }

    public static String get(String key) {
        return JcacheClients.getInstance().get(key);
    }

    public static void getAllCache() {
        JcacheClients.getInstance().getAllCache();
    }

}
