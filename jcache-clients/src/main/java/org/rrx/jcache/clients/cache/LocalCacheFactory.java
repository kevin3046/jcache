package org.rrx.jcache.clients.cache;

import org.rrx.jcache.clients.cache.entrylist.EntryCache;

import java.util.ServiceLoader;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/11 16:34
 * @Description:
 */
public class LocalCacheFactory {


    public static LocalCache getLoaclCache(){
        ServiceLoader<LocalCache> localCaches = ServiceLoader.load(LocalCache.class);
        if(localCaches.iterator().hasNext()){
            return localCaches.iterator().next();
        }
        return new EntryCache();
    }
}
