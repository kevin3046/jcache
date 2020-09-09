package org.rrx.jcache.clients.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.PriorityOrdered;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/6 14:27
 * @Description:InstantiationAwareBeanPostProcessorAdapter 实现了 BeanPostProcessor
 */
public class TestAdapter extends InstantiationAwareBeanPostProcessorAdapter implements
        BeanClassLoaderAware, PriorityOrdered,
        BeanFactoryPostProcessor, InitializingBean,
        ApplicationContextAware, MergedBeanDefinitionPostProcessor,
        BeanFactoryAware, ApplicationListener {


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("ApplicationListener.onApplicationEvent");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("BeanFactoryAware.setBeanFactory");
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        System.out.println("MergedBeanDefinitionPostProcessor.postProcessMergedBeanDefinition");
    }

    @Override
    public void resetBeanDefinition(String beanName) {
        System.out.println("MergedBeanDefinitionPostProcessor.resetBeanDefinition");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("ApplicationContextAware.setApplicationContext");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean.afterPropertiesSet");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("BeanFactoryPostProcessor.postProcessBeanFactory");

    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("BeanClassLoaderAware.setBeanClassLoader");
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("InstantiationAwareBeanPostProcessorAdapter.postProcessBeforeInstantiation");
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println("InstantiationAwareBeanPostProcessorAdapter.postProcessAfterInstantiation");
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public int getOrder() {
        System.out.println("PriorityOrdered.getOrder");
        return 0;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        System.out.println("InstantiationAwareBeanPostProcessorAdapter.postProcessProperties");
        return super.postProcessProperties(pvs, bean, beanName);
    }
}
