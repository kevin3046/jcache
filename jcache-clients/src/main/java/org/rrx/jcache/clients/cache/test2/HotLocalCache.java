//package org.rrx.jcache.clients.cache.test2;
//
//import org.rrx.jcache.clients.cache.HotsCache;
//import org.rrx.jcache.commons.dto.CacheBean;
//
//import java.util.*;
//
///**
// * @Auther: kevin3046@163.com
// * @Date: 2020/9/9 18:53
// * @Description:
// */
//public class HotLocalCache {
//
//    private Map<String, HotsCache.DLinkedNode> cache = new HashMap<String, HotsCache.DLinkedNode>();
//
//
//    private Map<Integer, HotsCache.DLinkedNode> index = new TreeMap<>();
//
//    public List<Integer> table = new ArrayList<>();
//
//    public class EntryNode {
//        public String key;//40
//        public CacheBean cacheBean;//实际存储值
//        public Long memory = 0L;//占用内存
//    }
//
//    public void put(String key, CacheBean cacheBean) {
//
//
//    }
//
//    public static void main(String[] args) {
////        ConcurrentSkipListSet<CacheBean> map = new ConcurrentSkipListSet<CacheBean>(new Comparator<CacheBean>() {
////            @Override
////            public int compare(CacheBean o1, CacheBean o2) {
////                return o1.hots.compareTo(o2.hots);
////            }
////        });
////        map.add(new CacheBean("123",100L,456L));
////        map.add(new CacheBean("456",100L,456L));
////        map.add(new CacheBean("789",200L,456L));
////
////        for(CacheBean cacheBean:map){
////            System.out.println(cacheBean.toString());
////        }
//
//        SkipList list = new SkipList();
//        System.out.println(list);
//        list.put(2, "yan");
//        list.put(1, "co");
//        list.put(3, "feng");
//        list.put(1, "cao");//测试同一个key值
//        list.put(4, "曹");
//        list.put(6, "丰");
//        list.put(5, "艳");
//        System.out.println(list);
//        System.out.println(list.size());
//
//    }
//
//}
