package org.rrx.jcache.clients.config.spring.annotation;

import org.rrx.jcache.commons.config.spring.annotation.EnableJcacheClientConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:26
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableJcacheClientConfig
@Import(JcacheClientsRegistrar.class)
public @interface EnableJcacheClients {

}
