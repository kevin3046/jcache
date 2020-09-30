package org.rrx.jcache.clients.cache.entrylist;

import org.rrx.jcache.clients.cache.LocalNode;
import org.rrx.jcache.commons.dto.CacheBean;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/11 16:07
 * @Description:
 */
public class DLinkedNode extends LocalNode {

    public DLinkedNode prev;//8
    public DLinkedNode next;//8

    public DLinkedNode() {

    }

    public DLinkedNode(String key, CacheBean value) {
        this.key = key;
        this.value = value;
    }
}
