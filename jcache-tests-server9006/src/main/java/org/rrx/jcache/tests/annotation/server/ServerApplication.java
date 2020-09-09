package org.rrx.jcache.tests.annotation.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/26 15:00
 * @Description:
 */
public class ServerApplication {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerConfig.class);
        context.start();
        System.in.read();
    }
}
