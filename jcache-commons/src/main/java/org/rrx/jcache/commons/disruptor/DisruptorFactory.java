package org.rrx.jcache.commons.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.rrx.jcache.commons.logging.LogFactory;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/8 15:50
 * @Description:
 */
public class DisruptorFactory {

    private static final Logger log = LogFactory.getLogger(DisruptorFactory.class);

    private DisruptorEventHandler handler;

    private Disruptor<StringEvent> disruptor;

    private StringEventProducer stringEventProducer;

    public DisruptorFactory(DisruptorEventHandler handler) {
        this.handler = handler;
    }

    public void start() {
        //创建一个线程工厂,用来提供线程,给消费者处理事件使用
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        //创建工单
        EventFactory<StringEvent> eventFactory = new StringEventFactoy();

        //环形队列的大小
        int ringBufferSize = 1024;

        disruptor = new Disruptor<>(
                eventFactory,
                ringBufferSize,
                threadFactory,
                ProducerType.SINGLE,
                new YieldingWaitStrategy());

        //连接消费端的方法
        disruptor.handleEventsWith(new StringEventHandler(handler));

        //disruptor 启动
        disruptor.start();

        //创建ringbuffer容器
        RingBuffer<StringEvent> ringBuffer = disruptor.getRingBuffer();

        //创建生产者
        stringEventProducer = new StringEventProducer(ringBuffer);

        log.info("DisruptorFactory start success");
    }

    public void onData(String value) {
        stringEventProducer.onData(value);
    }

    public void shutdown() {
        disruptor.shutdown();
    }
}
