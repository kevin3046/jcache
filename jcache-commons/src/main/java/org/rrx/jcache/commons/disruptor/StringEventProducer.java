package org.rrx.jcache.commons.disruptor;

import com.lmax.disruptor.RingBuffer;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/8 16:56
 * @Description:事件生产者
 */
public class StringEventProducer {

    public final RingBuffer<StringEvent> ringBuffer;


    public StringEventProducer(RingBuffer<StringEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * 生产数据
     *
     * @param data
     */
    public void onData(String data) {

        //取出事件队列的一个槽
        long seq = ringBuffer.next();
        try {
            //取出一个空的事件
            StringEvent stringEvent = ringBuffer.get(seq);
            //放入数据
            stringEvent.setValue(data);
        } finally {
            //发布事件
            ringBuffer.publish(seq);
        }
    }
}
