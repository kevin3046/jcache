package org.rrx.jcache.server.config.spring.annotation;

import org.rrx.jcache.server.InitJcacheServer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/22 16:54
 * @Description:
 */
public class JcacheServerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(InitJcacheServer.class.getName());
        registry.registerBeanDefinition(InitJcacheServer.class.getName(), beanDefinition);
    }
}
