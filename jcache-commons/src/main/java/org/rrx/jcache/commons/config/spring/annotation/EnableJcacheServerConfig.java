package org.rrx.jcache.commons.config.spring.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:24
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(JcacheServerConfigConfigurationRegistrar.class)
public @interface EnableJcacheServerConfig {
}
