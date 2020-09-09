//package org.rrx.jcache.server.hots;
//
//import org.rrx.jcache.commons.logging.LogFactory;
//import org.slf4j.Logger;
//
//import java.util.concurrent.atomic.AtomicLong;
//
///**
// * @Auther: kevin3046@163.com
// * @Date: 2020/8/27 19:01
// * @Description:
// */
//public class LongAdder {
//
//    private static final Logger log = LogFactory.getLogger(LongAdder.class);
//
//    private Entry head;
//
//    private Entry tail;
//
//    class Entry {
//        public Entry prev;
//        public Entry next;
//        public Long counts = 0L;
//
//        @Override
//        public String toString() {
//            return "Entry{ counts=" + counts + "}";
//        }
//    }
//
//    //当前热度值
//    public AtomicLong counts = new AtomicLong(0);
//
//    //环形链表存放数组
//    private Entry[] table = new Entry[10];
//
//    //环形链表的总大小
//    private final int size = 10;
//
//    public LongAdder() {
//        head = new LongAdder.Entry();
//        tail = new LongAdder.Entry();
//
//        head.next = tail;
//        tail.prev = head;
//
//        LongAdder.Entry current = head;
//        for (int i = 0; i < size; i++) {
//            LongAdder.Entry entry = new LongAdder.Entry();
//            current.next = entry;
//            entry.prev = current;
//            current = current.next;
//            table[i] = entry;
//        }
//        current.next = tail;
//        tail.prev = current;
//    }
//
//    public void add(Long counts) {
//
//
//        log.info("insert counts=" + counts);
//
//        //取出最后一个节点
//        LongAdder.Entry lastEntry = tail.prev;
//        lastEntry.counts = counts;
//        //倒数第二个节点指向tail
//        tail.prev.prev.next = tail;
//        tail.prev = tail.prev.prev;
//
//        //把lastEntry的下一个节点,指向原本的首节点
//        lastEntry.next = head.next;
//        head.next.prev = lastEntry;
//        //首节点改为lastEntry
//        head.next = lastEntry;
//
//        //debug 打印一下
//        LongAdder.Entry current = head;
//        while (current != null) {
//            log.info(" " + current.counts);
//            current = current.next;
//        }
//    }
//
//    public Long totalCounts() {
//        Long result = 0L;
//        for (int i = 0; i < size; i++) {
//            result += table[i].counts;
//        }
//        return result;
//    }
//
////    public static void main(String[] args) {
////        LongAdder longAdder = new LongAdder();
////        for (int i = 10; i <= 30; i++) {
////            longAdder.counts.set((long) i);
////            longAdder.add(longAdder.counts.longValue());
////        }
////        System.out.println(longAdder.toString());
////        System.out.println(longAdder.totalCounts());
////    }
//}
