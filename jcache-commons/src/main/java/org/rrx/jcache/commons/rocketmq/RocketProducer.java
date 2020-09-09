package org.rrx.jcache.commons.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.rrx.jcache.commons.config.properties.RocketmqProperties;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.commons.utils.CommonUtils;
import org.slf4j.Logger;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/27 18:47
 * @Description:
 */
public class RocketProducer {

    private static final Logger log = LogFactory.getLogger(RocketProducer.class);

    private RocketmqProperties rocketmqProperties;

    private DefaultMQProducer defaultMQProducer;

    public RocketProducer(RocketmqProperties rocketmqProperties) {
        this.rocketmqProperties = rocketmqProperties;
    }

    public void initProducer() {
        try {

            String producerGroupName = rocketmqProperties.getProducerGroupName();
            String instanceName = rocketmqProperties.getProducerGroupName() + "_" + CommonUtils.generateId();

            DefaultMQProducer producer = new DefaultMQProducer(producerGroupName);
            producer.setNamesrvAddr(rocketmqProperties.getNamesrvAddr());
            //producer.setCreateTopicKey("AUTO_CREATE_TOPIC_KEY");
            //如果需要同一个 jvm 中不同的 producer 往不同的 mq 集群发送消息，需要设置不同的 instanceName
            producer.setInstanceName(instanceName);
            //如果发送消息的最大限制
            //producer.setMaxMessageSize(applicationConfig.getMaxMessageSize());
            //如果发送消息超时时间
            producer.setSendMsgTimeout(rocketmqProperties.getSendMsgTimeout());
            //如果发送消息失败，设置重试次数，默认为 2 次
            producer.setRetryTimesWhenSendFailed(rocketmqProperties.getRetryTimesWhenSendFailed());
            producer.start();
            this.defaultMQProducer = producer;
            log.info("RocketProducer start success");
        } catch (Exception e) {
            throw new RuntimeException("defaultMQProducer exception", e);
        }
    }

    public void close() {
        this.defaultMQProducer.shutdown();
    }


    public boolean send(String topic, String tags, String msg) {
        try {
            Message message = new Message(
                    topic,
                    tags,
                    CommonUtils.generateId(),
                    msg.getBytes(RemotingHelper.DEFAULT_CHARSET));

            SendResult sendResult = this.defaultMQProducer.send(message);
            boolean ret = SendStatus.SEND_OK.equals(sendResult.getSendStatus());
            if (ret) {
                log.debug("send msg ok");
                return true;
            } else {
                log.debug("send msg fail");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug(e.getMessage());
            return false;
        }
    }
}
