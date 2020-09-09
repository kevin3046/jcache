package org.rrx.jcache.commons.disruptor;

import com.lmax.disruptor.EventHandler;
import org.rrx.jcache.commons.logging.LogFactory;
import org.slf4j.Logger;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/8 16:50
 * @Description:消费者--事件的具体实现
 */
public class StringEventHandler implements EventHandler<StringEvent> {

    private static final Logger log = LogFactory.getLogger(StringEventHandler.class);

    private DisruptorEventHandler handler;

    public StringEventHandler(DisruptorEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onEvent(StringEvent stringEvent, long sequence, boolean endOfBatch) throws Exception {
        log.debug("consumer value:" + stringEvent.getValue() + ",sequence:" + sequence);
        handler.onEvent(stringEvent, sequence, endOfBatch);
    }
}
