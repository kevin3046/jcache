package org.rrx.jcache.clients.cache.test1;

import java.util.Random;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/10 16:56
 * @Description:
 */
public class SkipList {

    public SkipListEntry head;  // First element of the top level
    public SkipListEntry tail;  // Last element of the top level

    public int n;       // number of entries in the Skip List
    public int h;       // Height

    public Random r;    // Coin toss


    // constructor
    public SkipList() {
        SkipListEntry p1, p2;

        // 创建一个 -oo 和一个 +oo 对象
        p1 = new SkipListEntry(SkipListEntry.negInf, null);
        p2 = new SkipListEntry(SkipListEntry.posInf, null);

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

    private SkipListEntry findEntry(String key) {

        SkipListEntry p;

        // 从head头节点开始查找
        p = head;

        while (true) {
            // 从左向右查找，直到右节点的key值大于要查找的key值
            while (p.right.key != SkipListEntry.posInf
                    && p.right.key.compareTo(key) <= 0) {
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

    public Integer get(String key) {

        SkipListEntry p;

        p = findEntry(key);

        if (p.key.equals(key)) {
            return p.value;
        } else {
            return null;
        }
    }

    public Integer put(String key, Integer value) {

        SkipListEntry p, q;
        int i = 0;

        // 查找适合插入的位子
        p = findEntry(key);

        // 如果跳跃表中存在含有key值的节点，则进行value的修改操作即可完成
        if (p.key.equals(key)) {
            Integer oldValue = p.value;
            p.value = value;
            return oldValue;
        }

        // 如果跳跃表中不存在含有key值的节点，则进行新增操作
        q = new SkipListEntry(key, value);
        q.left = p;
        q.right = p.right;
        p.right.left = q;
        p.right = q;

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
            SkipListEntry z = new SkipListEntry(key, null);

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
        return null;
    }

    private void addEmptyLevel() {

        SkipListEntry p1, p2;

        p1 = new SkipListEntry(SkipListEntry.negInf, null);
        p2 = new SkipListEntry(SkipListEntry.posInf, null);

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

    public Integer remove(String key) {

        SkipListEntry p, q;

        p = findEntry(key);

        if (!p.key.equals(key)) {
            return null;
        }

        Integer oldValue = p.value;
        while (p != null) {
            q = p.up;
            p.left.right = p.right;
            p.right.left = p.left;
            p = q;
        }

        return oldValue;
    }

    public void print() {
        SkipListEntry p;
        SkipListEntry t;

        // 从head头节点开始查找
        p = head;

        while (true) {

            System.out.print(p.key);
            t = p;
            while (p.right != null) {
                System.out.print("--" + p.right.key);
                p = p.right;
            }
            p = t;
            if (p.down != null) {
                p = p.down;
                System.out.println("");
            } else {
                break;
            }
            // 从左向右查找，直到右节点的key值大于要查找的key值
//            while(p.right.key != SkipListEntry.posInf
//                    && p.right.key.compareTo(key) <= 0) {
//                p = p.right;
//            }
//
//            // 如果有更低层的节点，则向低层移动
//            if(p.down != null) {
//                p = p.down;
//            } else {
//                break;
//            }
        }
    }

    public static void main(String[] args) {
        SkipList skipList = new SkipList();
        for (int i = 0; i <= 20; i++) {
            skipList.put("" + i, i * 10);
        }


        skipList.print();
        System.out.println(skipList.get("8"));

    }
}
