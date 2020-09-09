package org.rrx.jcache.commons.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/8 16:49
 * @Description:事件工厂
 */
public class StringEventFactoy implements EventFactory<StringEvent> {

    @Override
    public StringEvent newInstance() {
        return new StringEvent();
    }
}
