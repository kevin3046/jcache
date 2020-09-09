package org.rrx.jcache.server.config.spring.annotation;

import org.rrx.jcache.commons.config.spring.annotation.EnableJcacheServerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/22 16:54
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableJcacheServerConfig
@Import(JcacheServerRegistrar.class)
public @interface EnableJcacheServer {

}
