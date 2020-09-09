package org.rrx.jcache.server.hots;

import org.rrx.jcache.commons.logging.LogFactory;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/27 19:01
 * @Description:
 */
public class LongWheel {

    private static final Logger log = LogFactory.getLogger(LongWheel.class);

    //当前热度值
    public AtomicLong counts = new AtomicLong(0);

    //空闲次数
    public AtomicInteger idleTimes = new AtomicInteger(0);

    private AtomicInteger ticks = new AtomicInteger(0);

    //最大空闲次数
    private final int maxLdleTime = 15;

    //环形链表的总大小
    private final int size = 10;

    private long[] table = new long[10];

    public void add(long counts) {
        table[ticks.get()] = counts;
        if (ticks.incrementAndGet() >= size) {
            ticks.set(0);
        }
        log.debug("LongWheel.table:" + Arrays.toString(table) + ",keepAliveTimes:" + idleTimes.get());
    }

    public boolean isCanRemove() {
        return idleTimes.get() >= maxLdleTime;
    }

    public long sum() {
        long sum = 0L;
        for (int i = 0; i < size; i++) {
            sum += table[i];
        }
        return sum;
    }

//    public static void main(String[] args) {
//        //100W 耗时3800～4400ms左右
//        ConcurrentHashMap<String, LongWheel> hotsCache = new ConcurrentHashMap<>();
//        for(int i=0;i<1000000;i++){
//            LongWheel longWheel = new LongWheel();
//            longWheel.add(i);
//            hotsCache.put("test-test-test-test-test-test-"+i,longWheel);
//        }
//        long start = System.currentTimeMillis();
//        for(String key:hotsCache.keySet()){
//            System.out.println(hotsCache.get(key).sum());
//        }
//        System.out.println("cost:"+(System.currentTimeMillis()-start));
//
//    }
}
