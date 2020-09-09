package org.rrx.jcache.commons.disruptor;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/8 15:58
 * @Description:
 */
public abstract class DisruptorEventHandler {

    protected abstract void onEvent(StringEvent stringEvent, long sequence, boolean endOfBatch);
}
