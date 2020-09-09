package org.rrx.jcache.clients;

import org.rrx.jcache.commons.config.properties.JcacheClientConfigProperties;
import org.rrx.jcache.commons.disruptor.DisruptorEventHandler;
import org.rrx.jcache.commons.disruptor.StringEvent;
import org.rrx.jcache.commons.rocketmq.RocketProducer;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/8 16:02
 * @Description:
 */
public class SendMessageHandler extends DisruptorEventHandler {

    private JcacheClientConfigProperties configProperties;

    private RocketProducer producer;


    public SendMessageHandler(JcacheClientConfigProperties configProperties, RocketProducer producer) {
        this.configProperties = configProperties;
        this.producer = producer;
    }

    @Override
    protected void onEvent(StringEvent stringEvent, long sequence, boolean endOfBatch) {

        producer.send(
                configProperties.getRocketmqProperties().getTopicKeyReport(),
                configProperties.getRocketmqProperties().getTopicTagsReport(),
                stringEvent.getValue());
    }
}
