package org.rrx.jcache.clients.cache;

import org.rrx.jcache.commons.dto.CacheBean;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/11 18:38
 * @Description:
 */
public abstract class LocalNode {

    // data
    public String key;
    public CacheBean value;
    public Long memory = 0L;//占用内存

}
