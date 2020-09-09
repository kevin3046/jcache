package org.rrx.jcache.commons.rocketmq;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/25 19:58
 * @Description:
 */
public abstract class AbstractListener<T> {

    private String topicName;
    private String tagName;
    private Class<T> clazz;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }
}
