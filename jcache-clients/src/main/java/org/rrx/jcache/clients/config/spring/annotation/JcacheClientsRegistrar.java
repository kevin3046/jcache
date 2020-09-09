package org.rrx.jcache.clients.config.spring.annotation;


import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:43
 * @Description:
 */
public class JcacheClientsRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(JcacheScanner.class.getName());
        registry.registerBeanDefinition(JcacheScanner.class.getName(), beanDefinition);
    }
}
