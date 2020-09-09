package org.rrx.jcache.clients.config.spring.annotation;

import org.rrx.jcache.commons.dto.Operate;

import java.lang.annotation.*;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/28 15:33
 * @Description:
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Jcache {

    Operate operate();
}