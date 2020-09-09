package org.rrx.jcache.tests.annotation.clients;

import org.rrx.jcache.clients.config.spring.annotation.EnableJcacheClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:48
 * @Description:
 */
@Configuration
@EnableJcacheClients
@PropertySource("classpath:jcache.properties")
@PropertySource("classpath:redis.properties")
public class ClientsConfig {

}
