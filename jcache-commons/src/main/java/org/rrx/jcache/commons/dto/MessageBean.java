package org.rrx.jcache.commons.dto;

import java.io.Serializable;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 16:52
 * @Description:
 */
public class MessageBean implements Serializable {

    private String appName;

    private String key;

    private Integer sendTime;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getSendTime() {
        return sendTime;
    }

    public void setSendTime(Integer sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "appName='" + appName + '\'' +
                ", key='" + key + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}
