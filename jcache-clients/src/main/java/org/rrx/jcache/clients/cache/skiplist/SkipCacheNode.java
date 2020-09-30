package org.rrx.jcache.clients.cache.skiplist;

import org.rrx.jcache.clients.cache.LocalNode;
import org.rrx.jcache.commons.dto.CacheBean;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/10 16:55
 * @Description:
 */
public class SkipCacheNode extends LocalNode {

    // links
    public SkipCacheNode up;
    public SkipCacheNode down;
    public SkipCacheNode left;
    public SkipCacheNode right;

    // special
    public static final Long negInf = Long.MIN_VALUE;
    public static final Long posInf = Long.MAX_VALUE;

    // constructor
    public SkipCacheNode(String key, CacheBean value) {
        this.key = key;
        this.value = value;
        memory = nodeMemory();
    }

    private Long nodeMemory() {
        Long memory = 0L;
        if (key != null) {
            memory += (key.getBytes().length * 2 + 40) * 2;
        }
        if (value != null) {
            if (value.value != null) {
                memory += value.value.getBytes().length * 2 + 40;
            }
            memory += 16L;//hots expireTime
        }
        memory += 6 * 8L;//value memory up down left right
        return memory;
    }
}
