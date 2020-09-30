package org.rrx.jcache.clients.cache.skiplist;

import org.rrx.jcache.clients.cache.LocalCache;
import org.rrx.jcache.clients.cache.entrylist.DLinkedNode;
import org.rrx.jcache.clients.cache.entrylist.EntryCache;
import org.rrx.jcache.commons.dto.CacheBean;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/10 16:56
 * @Description:
 */
public class SkipCache implements LocalCache<SkipCacheNode> {

    private Long capacity = 0L;
    private Long maxCapacity = 64L * 1024 * 1024;//最大64MB

    private Map<String, SkipCacheNode> cache = new ConcurrentHashMap<String, SkipCacheNode>();

    private SkipCacheNode head;  // First element of the top level
    private SkipCacheNode tail;  // Last element of the top level

    private int n;       // number of entries in the Skip List
    private int h;       // Height

    private Random r;    // Coin toss

    // constructor
    public SkipCache() {
        SkipCacheNode p1, p2;

        // 创建一个 -oo 和一个 +oo 对象
        p1 = new SkipCacheNode(null, new CacheBean(null, SkipCacheNode.negInf));
        p2 = new SkipCacheNode(null, new CacheBean(null, SkipCacheNode.posInf));

        // 将 -oo 和 +oo 相互连接
        p1.right = p2;
        p2.left = p1;

        // 给 head 和 tail 初始化
        head = p1;
        tail = p2;

        n = 0;
        h = 0;
        r = new Random();
    }

    @Override
    public Map<String, SkipCacheNode> getCache() {
        return cache;
    }

    public CacheBean get(String key) {
        SkipCacheNode skipCacheNode = cache.get(key);
        if (skipCacheNode == null) {
            return null;
        }
        return skipCacheNode.value;
    }

    public void put(String key, CacheBean cacheBean) {
        SkipCacheNode entry = cache.get(key);
        if (entry == null) {
            entry = insert(key, cacheBean);
            cache.put(key, entry);
            this.capacity += entry.memory;
        } else {
            CacheBean value = entry.value;
            if (!value.hots.equals(cacheBean.hots)) {
                removeNode(entry);
                this.capacity -= entry.memory;
                entry = insert(key, cacheBean);
                cache.put(key, entry);
                this.capacity += entry.memory;
            } else {
                entry.value = cacheBean;
            }
        }
        //如果当前内存,大于最大内存,则丢弃头部数据
        while ((this.capacity > this.maxCapacity)) {
            //删除
            SkipCacheNode temp = removeHead();
            cache.remove(temp.key);
            this.capacity -= temp.memory;
        }
    }

    public void remove(String key) {

        SkipCacheNode p, q;
        p = cache.get(key);
        if (p == null) {
            return;
        }

        if (!p.key.equals(key)) {
            return;
        }
        this.capacity -= p.memory;
        cache.remove(key);
        while (p != null) {
            q = p.up;
            p.left.right = p.right;
            p.right.left = p.left;
            p = q;
        }
    }


    private SkipCacheNode findPos(CacheBean cacheBean) {

        SkipCacheNode p;

        // 从head头节点开始查找
        p = head;

        while (true) {
            // 从左向右查找，直到右节点的key值大于要查找的key值
            while (!p.right.value.hots.equals(SkipCacheNode.posInf)
                    && p.right.value.hots.compareTo(cacheBean.hots) <= 0) {
                p = p.right;
            }

            // 如果有更低层的节点，则向低层移动
            if (p.down != null) {
                p = p.down;
            } else {
                break;
            }
        }

        // 返回p，！注意这里p的key值是小于等于传入key的值的（p.key <= key）
        return p;
    }


    private SkipCacheNode insert(String key, CacheBean value) {

        SkipCacheNode p, q;
        int i = 0;

        // 查找适合插入的位置
        p = findPos(value);

        // 如果跳跃表中不存在含有key值的节点，则进行新增操作
        q = new SkipCacheNode(key, value);

        SkipCacheNode result = q;
        q.left = p;
        q.right = p.right;
        p.right.left = q;
        p.right = q;

        if (p.value.hots.equals(q.value.hots)) {
            return result;
        }

        // 再使用随机数决定是否要向更高level攀升
        while (r.nextDouble() < 0.5) {

            // 如果新元素的级别已经达到跳跃表的最大高度，则新建空白层
            if (i >= h) {
                addEmptyLevel();
            }

            // 从p向左扫描含有高层节点的节点
            while (p.up == null) {
                p = p.left;
            }
            p = p.up;

            // 新增和q指针指向的节点含有相同key值的节点对象
            // 这里需要注意的是除底层节点之外的节点对象是不需要value值的
            SkipCacheNode z = new SkipCacheNode(null, new CacheBean(null, value.hots));

            z.left = p;
            z.right = p.right;
            p.right.left = z;
            p.right = z;

            z.down = q;
            q.up = z;

            q = z;
            i = i + 1;
        }

        n = n + 1;

        // 返回null，没有旧节点的value值
        return result;
    }

    private void addEmptyLevel() {

        SkipCacheNode p1, p2;

        p1 = new SkipCacheNode(null, new CacheBean(null, SkipCacheNode.negInf));
        p2 = new SkipCacheNode(null, new CacheBean(null, SkipCacheNode.posInf));

        p1.right = p2;
        p1.down = head;

        p2.left = p1;
        p2.down = tail;

        head.up = p1;
        tail.up = p2;

        head = p1;
        tail = p2;

        h = h + 1;
    }

    private void removeNode(SkipCacheNode node) {

        SkipCacheNode p, q;
        p = node;
        if (p == null) {
            return;
        }

        if (!p.key.equals(node.key)) {
            return;
        }

        while (p != null) {
            q = p.up;
            p.left.right = p.right;
            p.right.left = p.left;
            p = q;
        }
    }


    private SkipCacheNode removeHead() {

        SkipCacheNode p, q;

        SkipCacheNode fhead = head;
        while (fhead.down != null) {
            fhead = fhead.down;
        }
        p = fhead.right;
        SkipCacheNode result = p;
        while (p != null) {
            q = p.up;
            p.left.right = p.right;
            p.right.left = p.left;
            p = q;
        }
        return p;

    }

    private SkipCacheNode removeTail() {

        SkipCacheNode p, q;

        SkipCacheNode lastTail = tail;
        while (lastTail.down != null) {
            lastTail = lastTail.down;
        }
        p = lastTail.left;
        SkipCacheNode result = p;
        while (p != null) {
            q = p.up;
            p.left.right = p.right;
            p.right.left = p.left;
            p = q;
        }
        return result;

    }

    public void print() {
        SkipCacheNode p;
        SkipCacheNode t;

        // 从head头节点开始查找
        p = head;

        while (true) {

            System.out.print("--[" + p.value.hots + "-" + p.key + "]");
            t = p;
            while (p.right != null) {
                System.out.print("--[" + p.right.value.hots + "-" + p.right.key + "]");
                p = p.right;
            }
            p = t;
            if (p.down != null) {
                p = p.down;
                System.out.println("");
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) {

        int num = 50000;
        long[] table = new long[num+1];
        Random r = new Random();
        for(int i=0;i<=num;i++){
            table[i] =  r.nextInt(num);
            //table[i] =  10000-i;
            //System.out.println(table[i]);
        }
        long start = System.currentTimeMillis();
        SkipCache skipList = new SkipCache();
        for(int i=0;i<=num;i++){
            skipList.put("key"+table[i],new CacheBean("value"+table[i],table[i],123L));
        }
        System.out.println((System.currentTimeMillis())-start);

        start = System.currentTimeMillis();
        EntryCache hotsCache = new EntryCache();
        for(int i=0;i<=num;i++){
            hotsCache.put("key"+table[i],new CacheBean("value"+table[i],table[i],123L));
        }
        System.out.println((System.currentTimeMillis())-start);


        start = System.currentTimeMillis();
        ConcurrentSkipListMap map = new ConcurrentSkipListMap();
        for(int i=0;i<=num;i++){
            map.put("key"+table[i],new CacheBean("value"+table[i],table[i],123L));
        }
        System.out.println((System.currentTimeMillis())-start);

//        skipList.print();
//
//        skipList.removeTail();
//        skipList.print();
//
//        skipList.removeTail();
//        skipList.print();
//
//
//        for(long i=0;i<=20;i++){
//            skipList.put(i,"key"+i+""+i,new CacheBean("value"+i+""+i,i+10L,123L));
//        }
//
//        skipList.print();
//        skipList.remove("key10",10L);
//        skipList.print();
//        System.out.println("==========");

//        SkipCache skipList = new SkipCache();
//        for (long i = 0; i <= 10; i++) {
//            skipList.put("key" + i, new CacheBean("value" + i, i, 123L));
//        }
//        skipList.print();
//
//        skipList.put("key5", new CacheBean("value5", 3L, 123L));
//
//        skipList.print();


    }
}
