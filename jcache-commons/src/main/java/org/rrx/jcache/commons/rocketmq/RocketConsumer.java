package org.rrx.jcache.commons.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.rrx.jcache.commons.config.properties.RocketmqProperties;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.commons.utils.CommonUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/25 19:42
 * @Description:
 */
public class RocketConsumer {

    private static final Logger log = LogFactory.getLogger(RocketConsumer.class);

    private RocketmqProperties rocketmqProperties;
    private Map<String, DefaultMQPushConsumer> consumerMap = new HashMap<>();

    public RocketConsumer(RocketmqProperties rocketmqProperties) {
        this.rocketmqProperties = rocketmqProperties;
    }

    public void registerListener(AbstractListener listener) {
        if (consumerMap.containsKey(listener.getTopicName())) {
            return;
        }
        try {
            //消费组名称
            String consumerGroupName = rocketmqProperties.getConsumerGroupName() + "_" + listener.getTopicName();
            //实例名称,每台机器要定义不一样的名称
            String instanceName = rocketmqProperties.getConsumerGroupName() + "_" + listener.getTopicName() + "_" + CommonUtils.generateId();

            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroupName);
            consumer.setNamesrvAddr(rocketmqProperties.getNamesrvAddr());
            consumer.setInstanceName(instanceName);
            //集群消费
            consumer.setMessageModel(MessageModel.CLUSTERING);
            /**
             * 一个新的订阅组第一次启动从队列的最后位置开始消费<br>
             * 后续再启动接着上次消费的进度开始消费
             */
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            //每次消费一条
            consumer.setConsumeMessageBatchMaxSize(1);
            //订阅topic tag
            consumer.subscribe(listener.getTopicName(), listener.getTagName());
            //注册普通消息
            if (listener instanceof AbstractConcurrentlyListener) {
                consumer.registerMessageListener((AbstractConcurrentlyListener) listener);
            } else {
                throw new RuntimeException("not match listener");
            }

            consumerMap.put(listener.getTopicName(), consumer);
            consumer.start();
            log.debug(listener.getTopicName() + " register success");
        } catch (Exception e) {
            throw new RuntimeException("registerListener exception", e);
        }

    }

    public void stop() {
        for (String topicName : consumerMap.keySet()) {
            consumerMap.get(topicName).shutdown();
        }
    }
}
