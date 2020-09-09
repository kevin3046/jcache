package org.rrx.jcache.commons.config.spring.annotation;

import java.lang.annotation.*;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/28 15:33
 * @Description:
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JcacheValue {

    String value();
}