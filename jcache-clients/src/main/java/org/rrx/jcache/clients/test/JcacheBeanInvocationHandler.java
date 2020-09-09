package org.rrx.jcache.clients.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/3 17:38
 * @Description:
 */
public class JcacheBeanInvocationHandler implements InvocationHandler {

    //private final String jcacheBeanName;

    private Object bean;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
