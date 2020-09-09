package org.rrx.jcache.commons.constants;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/28 14:26
 * @Description:
 */
public class CommonConstants {

    /**
     * etcd 常量
     **/
    //全路径 /jcache/{$appName}/hots_dir
    public static final String ETCD_APP_HOTSDIR = "/hots_dir";

    public static final String ETCD_CONFIG = "/config";

    public static final String ETCD_CONFIG_APP = "/app";

    /**
     * redis 常量
     **/
    //redis appName有序列集合键名
    //全路径jcache_server:{$appName}:hots_${ticks} score member
    public static final String REDIS_HOTS_KEY = "hots_key_";


    public static final String REDIS_HOTS_TICKS = "hots_ticks";


    public static String getHotsDir(String appName) {
        return "/" + appName + ETCD_APP_HOTSDIR;
    }

    public static String getHotsDirKey(String appName, String key) {
        return "/" + appName + ETCD_APP_HOTSDIR + "/" + key;
    }

    public static String getRedisHotsKey(String appName, Long ticks) {
        return appName + ":" + REDIS_HOTS_KEY + ticks;
    }

}
