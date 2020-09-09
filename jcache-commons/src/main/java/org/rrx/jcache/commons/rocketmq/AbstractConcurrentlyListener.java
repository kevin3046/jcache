package org.rrx.jcache.commons.rocketmq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.rrx.jcache.commons.dto.MessageBean;
import org.rrx.jcache.commons.logging.LogFactory;
import org.slf4j.Logger;

import java.util.List;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/25 19:45
 * @Description:
 */
public abstract class AbstractConcurrentlyListener extends AbstractListener implements MessageListenerConcurrently {

    private static final Logger log = LogFactory.getLogger(AbstractConcurrentlyListener.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        //setConsumeMessageBatchMaxSize=1的情况下,List<MessageExt> msgs的队列大小只有1个
        long startTime = System.currentTimeMillis();
        try {
            for (MessageExt msg : msgs) {
                String body = new String(msg.getBody());
                MqMessageBean mqMessageBean = new MqMessageBean();
                mqMessageBean.setReconsumeTimes(msg.getReconsumeTimes());
                mqMessageBean.setMsgId(msg.getMsgId());
                mqMessageBean.setKeys(msg.getKeys());
                mqMessageBean.setBody(JSON.parseObject(body, MessageBean.class));
                log.debug("consumeMessage:{}", mqMessageBean.toString());
                onReceived(mqMessageBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        } finally {
            log.debug("cost------>" + (System.currentTimeMillis() - startTime));
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public abstract void onReceived(MqMessageBean mqMessageBean) throws Exception;
}
