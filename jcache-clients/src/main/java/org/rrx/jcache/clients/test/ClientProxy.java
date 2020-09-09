package org.rrx.jcache.clients.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/4 11:45
 * @Description:
 */
public class ClientProxy implements InvocationHandler {

    private Object target;

    public ClientProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before");
        Object rs = method.invoke(target, args);
        System.out.println("after");
        return rs;
    }
}
