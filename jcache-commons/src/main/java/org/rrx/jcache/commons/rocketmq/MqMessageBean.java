package org.rrx.jcache.commons.rocketmq;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/26 14:00
 * @Description:
 */
public class MqMessageBean<T> {

    private Integer reconsumeTimes;//重复消费次数

    private String msgId;//消息ID

    private String keys;//消息的唯一key,生产的时候传入

    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Integer getReconsumeTimes() {
        return reconsumeTimes;
    }

    public void setReconsumeTimes(Integer reconsumeTimes) {
        this.reconsumeTimes = reconsumeTimes;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "MqMessageBean{" +
                "reconsumeTimes=" + reconsumeTimes +
                ", msgId='" + msgId + '\'' +
                ", keys='" + keys + '\'' +
                ", body=" + body +
                '}';
    }
}
