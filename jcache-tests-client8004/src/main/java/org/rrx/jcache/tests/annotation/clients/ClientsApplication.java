package org.rrx.jcache.tests.annotation.clients;

import org.rrx.jcache.clients.utils.JcacheUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:45
 * @Description:
 */
public class ClientsApplication {


    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientsConfig.class);
        context.start();
        JcacheUtils.set("test", "11111111111111");
        JcacheUtils.set("test1", "222222222");
        JcacheUtils.set("test2", "33333333");

//
        for (int i = 0; i < 10; i++) {
            System.out.println(JcacheUtils.get("test"));
        }
        Thread.sleep(3000L);
        for (int i = 0; i < 20; i++) {
            System.out.println(JcacheUtils.get("test1"));
        }
        Thread.sleep(3000L);
        for (int i = 0; i < 30; i++) {
            System.out.println(JcacheUtils.get("test2"));
        }
//        Thread.sleep(3000L);
//        for (int i = 0; i < 25; i++) {
//            System.out.println(JcacheUtils.get("test"));
//        }
//
//        Thread.sleep(5000L);
//        for (int i = 0; i < 50; i++) {
//            System.out.println(JcacheUtils.get("test1"));
//        }
//        System.out.println(SetcdClientUtils.putValue("/foo", "123"));
//        System.out.println(SetcdClientUtils.getValue("/foo"));
        //System.in.read();
    }
}
