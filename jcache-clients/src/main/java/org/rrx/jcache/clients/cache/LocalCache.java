package org.rrx.jcache.clients.cache;

import org.rrx.jcache.clients.cache.entrylist.DLinkedNode;
import org.rrx.jcache.commons.dto.CacheBean;

import java.util.Map;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/11 16:02
 * @Description:
 */
public interface LocalCache<T extends LocalNode> {


    CacheBean get(String key);

    void put(String key, CacheBean cacheBean);

    void remove(String key);

    Map<String, T> getCache();

}