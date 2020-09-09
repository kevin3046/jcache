package org.rrx.jcache.clients.test;

import org.springframework.beans.factory.FactoryBean;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/6 14:38
 * @Description:
 */
public class TestFactoryBean implements FactoryBean {

    @Override
    public Object getObject() throws Exception {
        System.out.println("TestFactoryBean.getObject");
        return new TestPreson();
    }

    @Override
    public Class<?> getObjectType() {
        System.out.println("TestFactoryBean.getObjectType");
        return TestPreson.class;
    }
}
