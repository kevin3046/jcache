package org.rrx.jcache.commons.config.spring.annotation;

import org.rrx.jcache.commons.config.properties.*;
import org.rrx.jcache.commons.utils.PropertySourcesUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/20 13:38
 * @Description:
 */
public class JcacheClientConfigConfigurationRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private ConfigurableEnvironment environment;

    public static String prefix = "jcache.";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(JcacheClientConfigProperties.class.getName());
        Map<String, Object> configurationProperties = PropertySourcesUtils.getSubProperties(environment.getPropertySources(), environment, prefix);

        ClientBaseProperties clientBaseProperties = new ClientBaseProperties();

        setClassProperties(configurationProperties, clientBaseProperties);

        EtcdProperties etcdProperties = new EtcdProperties();
        setClassProperties(configurationProperties, etcdProperties);

        JedisProperties jedisProperties = new JedisProperties();
        setClassProperties(configurationProperties, jedisProperties);

        RedisProperties redisProperties = new RedisProperties();
        setClassProperties(configurationProperties, redisProperties);

        RocketmqProperties rocketmqProperties = new RocketmqProperties();
        setClassProperties(configurationProperties, rocketmqProperties);

        beanDefinition.getPropertyValues().addPropertyValue("clientBaseProperties", clientBaseProperties);

        beanDefinition.getPropertyValues().addPropertyValue("etcdProperties", etcdProperties);

        beanDefinition.getPropertyValues().addPropertyValue("rocketmqProperties", rocketmqProperties);

        registry.registerBeanDefinition(JcacheClientConfigProperties.class.getName(), beanDefinition);

    }

    private void setClassProperties(Map<String, Object> configurationProperties, Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            JcacheValue jcacheValue = field.getAnnotation(JcacheValue.class);
            if (jcacheValue == null) {
                continue;
            }
            field.setAccessible(true);
            Object value = configurationProperties.get(jcacheValue.value());
            if (value == null) {
                continue;
            }
            try {
                String type = field.getType().toString();
                if (type.endsWith("int")) {
                    field.set(object, (int) (value));
                } else if (type.endsWith("Integer")) {
                    field.set(object, Integer.valueOf(value.toString()));
                } else if (type.endsWith("long")) {
                    field.set(object, (long) value);
                } else if (type.endsWith("Long")) {
                    field.set(object, Long.valueOf(value.toString()));
                } else if (type.endsWith("boolean") || type.endsWith("Boolean")) {
                    field.set(object, Boolean.valueOf(value.toString()));
                } else {
                    field.set(object, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        this.environment = (ConfigurableEnvironment) environment;
    }
}
