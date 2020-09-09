package org.rrx.jcache.clients.config.spring.annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.rrx.jcache.clients.JcacheClients;
import org.rrx.jcache.commons.dto.Operate;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/4 14:09
 * @Description:
 */
public class JcacheInterceptor implements MethodInterceptor {

    private JcacheClients jcacheClients;

    public JcacheInterceptor(JcacheClients jcacheClients) {
        this.jcacheClients = jcacheClients;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass =
                invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null;
        Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
        if (specificMethod != null && !specificMethod.getDeclaringClass().equals(Object.class)) {
            final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
            final Jcache jcacheAnnotation = getAnnotation(method, targetClass, Jcache.class);
            if (jcacheAnnotation != null) {
                return handleInvocation(invocation, jcacheAnnotation);
            }
        }
        return invocation.proceed();
    }

    public Object handleInvocation(MethodInvocation invocation, Jcache jcacheAnnotation) throws Throwable {
        Operate operate = jcacheAnnotation.operate();
        if (Operate.GET.equals(operate)) {
            Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
            if (parameterTypes.length == 0) {
                throw new RuntimeException("method parameterTypes.length>1");
            }
            Class<?> parameterType = parameterTypes[0];
            if (!parameterType.getName().equals("java.lang.String")) {
                throw new RuntimeException("method parameterTypes key must be java.lang.String");
            }
            if (!invocation.getMethod().getReturnType().getName().equals("java.lang.String")) {
                throw new RuntimeException("method returnType must be java.lang.String");
            }

            Object argument = invocation.getArguments()[0];
            if (argument != null) {
                String key = String.valueOf(argument);
                String result = jcacheClients.get(key);
                if (!StringUtils.isEmpty(result)) {
                    jcacheClients.sendMessage(key);
                    return result;
                } else {
                    Object ret = invocation.proceed();
                    if (ret != null) {
                        jcacheClients.sendMessage(key);
                    }
                    return ret;
                }
            }

        } else if (Operate.SET.equals(operate) || Operate.EXPIRE.equals(operate) || Operate.DELETE.equals(operate)) {
            Object ret = invocation.proceed();

            Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
            if (parameterTypes.length < 1) {
                throw new RuntimeException("method parameterTypes.length<1");
            }
            Class<?> parameterType = parameterTypes[0];
            if (!parameterType.getName().equals("java.lang.String")) {
                throw new RuntimeException("method parameterTypes key must be java.lang.String");
            }
            Object argument = invocation.getArguments()[0];
            if (argument != null) {
                String key = String.valueOf(argument);
                jcacheClients.removeCache(key);
            }
            return ret;
        }
        return invocation.proceed();
    }

    public <T extends Annotation> T getAnnotation(Method method, Class<?> targetClass, Class<T> annotationClass) {
        return Optional.ofNullable(method).map(m -> m.getAnnotation(annotationClass))
                .orElse(Optional.ofNullable(targetClass).map(t -> t.getAnnotation(annotationClass)).orElse(null));
    }
}
