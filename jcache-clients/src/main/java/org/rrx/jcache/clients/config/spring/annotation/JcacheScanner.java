package org.rrx.jcache.clients.config.spring.annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.rrx.jcache.clients.JcacheClients;
import org.rrx.jcache.clients.utils.CollectionUtils;
import org.rrx.jcache.clients.utils.SpringProxyUtils;
import org.rrx.jcache.commons.config.properties.JcacheClientConfigProperties;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/4 14:08
 * @Description: http://seata.io/zh-cn/blog/seata-at-mode-start.html
 */
public class JcacheScanner extends AbstractAutoProxyCreator
        implements InitializingBean, ApplicationContextAware,
        DisposableBean {

    private static final Set<String> PROXYED_SET = new HashSet<>();

    private ApplicationContext applicationContext;

    private JcacheClientConfigProperties configProperties;

    private MethodInterceptor interceptor;

    private MethodInterceptor jcacheInterceptor;

    private JcacheClients jcacheClients;


    @Override
    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        try {
            synchronized (PROXYED_SET) {
                if (PROXYED_SET.contains(beanName)) {
                    return bean;
                }
                interceptor = null;

                Class<?> serviceInterface = SpringProxyUtils.findTargetClass(bean);
                Class<?>[] interfacesIfJdk = SpringProxyUtils.findInterfaces(bean);

                if (!existsAnnotation(new Class[]{serviceInterface})
                        && !existsAnnotation(interfacesIfJdk)) {
                    return bean;
                }

                if (interceptor == null) {
                    if (jcacheInterceptor == null) {
                        jcacheInterceptor = new JcacheInterceptor(jcacheClients);
                    }
                    interceptor = jcacheInterceptor;
                }
                if (!AopUtils.isAopProxy(bean)) {
                    bean = super.wrapIfNecessary(bean, beanName, cacheKey);
                } else {
                    AdvisedSupport advised = SpringProxyUtils.getAdvisedSupport(bean);
                    Advisor[] advisor = buildAdvisors(beanName, getAdvicesAndAdvisorsForBean(null, null, null));
                    for (Advisor avr : advisor) {
                        advised.addAdvisor(0, avr);
                    }
                }
                PROXYED_SET.add(beanName);
                return bean;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource customTargetSource) throws BeansException {
        return new Object[]{interceptor};
    }

    @Override
    public void destroy() throws Exception {
        ShutdownHook.getInstance().destroyAll();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jcacheClients = new JcacheClients(configProperties);
        jcacheClients.initClients();
        registerSpringShutdownHook();
    }

    private void registerSpringShutdownHook() {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext).registerShutdownHook();
            ShutdownHook.removeRuntimeShutdownHook();
        }
        ShutdownHook.getInstance().addDisposable(jcacheClients);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
        this.configProperties = (JcacheClientConfigProperties) applicationContext.getBean(JcacheClientConfigProperties.class.getName());
    }


    private boolean existsAnnotation(Class<?>[] classes) {
        if (CollectionUtils.isNotEmpty(classes)) {
            for (Class<?> clazz : classes) {
                if (clazz == null) {
                    continue;
                }
                Jcache trxAnno;
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    trxAnno = method.getAnnotation(Jcache.class);
                    if (trxAnno != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
