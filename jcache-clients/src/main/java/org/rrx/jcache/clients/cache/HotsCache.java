package org.rrx.jcache.clients.cache;

import org.rrx.jcache.commons.dto.CacheBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/24 13:51
 * @Description:LRU策略,最大保存64MB的缓存
 */
public class HotsCache {

    private Long capacity;
    private Long maxCapacity = 64L * 1024 * 1024;//最大64MB
    private Map<String, HotsCache.DLinkedNode> cache = new HashMap<String, HotsCache.DLinkedNode>();
    private HotsCache.DLinkedNode head;
    private HotsCache.DLinkedNode tail;

    public class DLinkedNode {
        String key;//40
        public CacheBean cacheBean;//实际存储值
        Long memory = 0L;//占用内存
        HotsCache.DLinkedNode prev;//8
        HotsCache.DLinkedNode next;//8

        public DLinkedNode() {

        }

        public DLinkedNode(String key, CacheBean cacheBean) {
            this.key = key;
            this.cacheBean = cacheBean;
        }
    }

    public HotsCache() {
        this.capacity = 0L;
        // 使用伪头部和伪尾部节点
        head = new HotsCache.DLinkedNode();
        tail = new HotsCache.DLinkedNode();
        head.next = tail;
        tail.prev = head;
    }

    public Map<String, DLinkedNode> getCache() {
        return cache;
    }

    public CacheBean get(String key) {
        HotsCache.DLinkedNode node = cache.get(key);
        if (node == null) {
            return null;
        }
        return node.cacheBean;
    }

    public void remove(String key) {
        HotsCache.DLinkedNode node = cache.get(key);
        if (node == null) {
            return;
        }
        removeNode(node);
        cache.remove(key);
        this.capacity -= node.memory;
    }

    private void insert(HotsCache.DLinkedNode newNode) {
        HotsCache.DLinkedNode current = head.next;
        if (cache.size() == 0 || head.next == tail) {
            head.next = newNode;
            tail.prev = newNode;
            newNode.prev = head;
            newNode.next = tail;
            return;
        }

        while (current != null && current != tail) {
            if (newNode.cacheBean.hots
                    >= current.cacheBean.hots) {
                newNode.prev = current.prev;
                newNode.next = current;
                current.prev.next = newNode;
                current.prev = newNode;
                return;
            }
            current = current.next;
        }
        newNode.prev = tail.prev;
        newNode.next = tail;
        tail.prev.next = newNode;
        tail.prev = newNode;
    }

    private Long nodeMemory(HotsCache.DLinkedNode node) {

        return (node.key.getBytes().length * 2 + 40) +
                (node.cacheBean.value.getBytes().length * 2 + 40) +
                5 * 8L;
    }

    public void put(String key, CacheBean cacheBean) {
        HotsCache.DLinkedNode node = cache.get(key);
        if (node == null) {
            // 如果 key 不存在，创建一个新的节点
            putNewNode(key, cacheBean);
        } else {
            updateNode(node, key, cacheBean);
        }
    }

    private void updateNode(HotsCache.DLinkedNode node, String key, CacheBean cacheBean) {
        if (!node.cacheBean.hots.equals(cacheBean.hots)) {
            //这里选择不删除cache的key,保证其他线程可以获取到
            removeNode(node);
            this.capacity -= node.memory;
            putNewNode(key, cacheBean);
            return;
        }
        node.cacheBean = cacheBean;
    }

    private void putNewNode(String key, CacheBean cacheBean) {

        HotsCache.DLinkedNode newNode = new HotsCache.DLinkedNode(key, cacheBean);
        Long memory = nodeMemory(newNode);
        newNode.memory = memory;

        //新node大于最大内存,则丢弃
        if (newNode.memory > this.maxCapacity) {
            return;
        }

        // 添加进哈希表
        insert(newNode);
        cache.put(key, newNode);
        this.capacity += newNode.memory;

        //如果当前内存,大于最大内存,则丢失尾部数据
        while ((this.capacity > this.maxCapacity)) {
            //删除
            DLinkedNode tail = removeTail();
            cache.remove(tail.key);
            this.capacity -= tail.memory;
        }
    }


    private void removeNode(HotsCache.DLinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private HotsCache.DLinkedNode removeTail() {
        HotsCache.DLinkedNode res = tail.prev;
        removeNode(res);
        return res;
    }
}
