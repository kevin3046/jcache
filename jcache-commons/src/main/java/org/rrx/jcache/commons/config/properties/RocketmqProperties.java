package org.rrx.jcache.commons.config.properties;

import org.rrx.jcache.commons.config.spring.annotation.JcacheValue;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/28 15:15
 * @Description:
 */
public class RocketmqProperties {

    @JcacheValue(value = "rocketmq.producerGroupName")
    private String producerGroupName = "producer_jcache";

    @JcacheValue(value = "rocketmq.consumerGroupName")
    private String consumerGroupName = "consumer_jcache";

    @JcacheValue(value = "rocketmq.namesrvAddr")
    private String namesrvAddr;

    @JcacheValue(value = "rocketmq.sendMsgTimeout")
    private Integer sendMsgTimeout = 3000;

    @JcacheValue(value = "rocketmq.retryTimesWhenSendFailed")
    private Integer retryTimesWhenSendFailed = 2;

    @JcacheValue(value = "rocketmq.topicKeyReport")
    private String topicKeyReport = "T_KEY_REPORT";

    @JcacheValue(value = "rocketmq.topicTagsReport")
    private String topicTagsReport = "T_KEY_REPORT";

    public String getProducerGroupName() {
        return producerGroupName;
    }

    public void setProducerGroupName(String producerGroupName) {
        this.producerGroupName = producerGroupName;
    }

    public String getConsumerGroupName() {
        return consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName) {
        this.consumerGroupName = consumerGroupName;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public Integer getSendMsgTimeout() {
        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(Integer sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    public Integer getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public void setRetryTimesWhenSendFailed(Integer retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
    }

    public String getTopicKeyReport() {
        return topicKeyReport;
    }

    public void setTopicKeyReport(String topicKeyReport) {
        this.topicKeyReport = topicKeyReport;
    }

    public String getTopicTagsReport() {
        return topicTagsReport;
    }

    public void setTopicTagsReport(String topicTagsReport) {
        this.topicTagsReport = topicTagsReport;
    }
}
