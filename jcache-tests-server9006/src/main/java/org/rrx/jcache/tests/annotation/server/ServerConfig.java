package org.rrx.jcache.tests.annotation.server;

import org.rrx.jcache.server.config.spring.annotation.EnableJcacheServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:48
 * @Description:
 */
@Configuration
@EnableJcacheServer
@PropertySource("classpath:jcache.properties")
public class ServerConfig {

}
