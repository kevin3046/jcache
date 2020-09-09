package org.rrx.jcache.clients.test;

import org.rrx.jcache.clients.config.spring.annotation.Jcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.core.BridgeMethodResolver.findBridgedMethod;
import static org.springframework.core.BridgeMethodResolver.isVisibilityBridgeMethodPair;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/3 14:52
 * @Description:
 */
public class JcacheAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor, BeanFactoryAware, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(JcacheAnnotationBeanPostProcessor.class);
    public static final String BEAN_NAME = "jcacheValueAnnotationBeanPostProcessor";

    private ConfigurableListableBeanFactory beanFactory;

    private Environment environment;

    private final Class<Jcache> annotationType = Jcache.class;

    private final ConcurrentMap<String, AnnotatedInjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>();


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findInjectionMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = findInjectionMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of @Jcache dependencies is failed", ex);
        }
        return pvs;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private InjectionMetadata findInjectionMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        AnnotatedInjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildAnnotatedMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect object class [" + clazz.getName() +
                                "] for annotation metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    private class AnnotatedInjectionMetadata extends InjectionMetadata {

        private final Collection<AnnotatedFieldElement> fieldElements;

        private final Collection<AnnotatedMethodElement> methodElements;

        public AnnotatedInjectionMetadata(Class<?> targetClass, Collection<AnnotatedFieldElement> fieldElements,
                                          Collection<AnnotatedMethodElement> methodElements) {
            super(targetClass, combine(fieldElements, methodElements));
            this.fieldElements = fieldElements;
            this.methodElements = methodElements;
        }

        public Collection<AnnotatedFieldElement> getFieldElements() {
            return fieldElements;
        }

        public Collection<AnnotatedMethodElement> getMethodElements() {
            return methodElements;
        }
    }

    /**
     * {@link Method} {@link InjectionMetadata.InjectedElement}
     */
    private class AnnotatedMethodElement extends InjectionMetadata.InjectedElement {

        private final Method method;

        private final Jcache annotation;

        AnnotatedMethodElement(Method method, PropertyDescriptor pd, Jcache annotation) {
            super(method, pd);
            this.method = method;
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {

            //Class<?> injectedType = pd.getPropertyType();

            Object injectedObject = getInjectedObject(annotation, bean, beanName, null, this);

            ReflectionUtils.makeAccessible(method);

            method.invoke(bean, injectedObject);

            //do something


        }

    }


    /**
     * {@link Field} {@link InjectionMetadata.InjectedElement}
     */
    public class AnnotatedFieldElement extends InjectionMetadata.InjectedElement {

        private final Field field;

        private final Jcache annotation;

        AnnotatedFieldElement(Field field, Jcache annotation) {
            super(field, null);
            this.field = field;
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {

            Class<?> injectedType = field.getType();

            Object injectedObject = getInjectedObject(annotation, bean, beanName, injectedType, this);

            ReflectionUtils.makeAccessible(field);

            field.set(bean, injectedObject);

            //do something
        }

    }


    /**
     * 查找需要注入的对象
     *
     * @param annotation
     * @param bean
     * @param beanName
     * @param injectedType
     * @param injectedElement
     * @return
     */
    private Object getInjectedObject(Jcache annotation, Object bean, String beanName, Class<?> injectedType, InjectionMetadata.InjectedElement injectedElement) {
        //String annotationValue = annotation.value();
        //查找不到，会报错，启动失败
        //String value = beanFactory.resolveEmbeddedValue(annotationValue);

//        Member member = injectedElement.getMember();
//        if (member instanceof Field) {
//            return BeanTypeConvertUtil.convertIfNecessary(beanFactory, (Field) member, value);
//        }
//
//        if (member instanceof Method) {
//            return BeanTypeConvertUtil.convertIfNecessary(beanFactory, (Method) member, value);
//        }
        return null;

//
//        IHello iHello=(IHello) Proxy.newProxyInstance(IHello.class.getClassLoader(),//加载接口的类加载器
//                new Class[]{IHello.class},
//                new MyInvocationHandler(new Hello()));

//        protected T newInstance(MapperProxy<T> mapperProxy) {
//            return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
//        }
//
//        public T newInstance(SqlSession sqlSession) {
//            final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
//            return newInstance(mapperProxy);
//        }

        //return Proxy.newProxyInstance(bean.getClass().getClassLoader(),new Class[]{RedisClientInter.class},new ClientProxy(bean));
    }

    private static <T> Collection<T> combine(Collection<? extends T>... elements) {
        List<T> allElements = new ArrayList<T>();
        for (Collection<? extends T> e : elements) {
            allElements.addAll(e);
        }
        return allElements;
    }

    private JcacheAnnotationBeanPostProcessor.AnnotatedInjectionMetadata buildAnnotatedMetadata(final Class<?> beanClass) {
        Collection<AnnotatedFieldElement> fieldElements = findAnnotatedFieldMetadata(beanClass);
        Collection<AnnotatedMethodElement> methodElements = findAnnotatedMethodMetadata(beanClass);
        return new JcacheAnnotationBeanPostProcessor.AnnotatedInjectionMetadata(beanClass, fieldElements, methodElements);
    }


    /**
     * Finds {@link InjectionMetadata.InjectedElement} Metadata from annotated {@link Jcache} fields
     *
     * @param beanClass The {@link Class} of Bean
     * @return non-null {@link List}
     */
    private List<AnnotatedFieldElement> findAnnotatedFieldMetadata(final Class<?> beanClass) {

        final List<AnnotatedFieldElement> elements = new LinkedList<AnnotatedFieldElement>();

        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                Jcache annotation = getAnnotation(field, annotationType);

                if (annotation != null) {

                    if (Modifier.isStatic(field.getModifiers())) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("@" + annotationType.getName() + " is not supported on static fields: " + field);
                        }
                        return;
                    }

                    elements.add(new JcacheAnnotationBeanPostProcessor.AnnotatedFieldElement(field, annotation));
                }

            }
        });

        return elements;

    }

    /**
     * Finds {@link InjectionMetadata.InjectedElement} Metadata from annotated {@link Jcache} methods
     *
     * @param beanClass The {@link Class} of Bean
     * @return non-null {@link List}
     */
    private List<AnnotatedMethodElement> findAnnotatedMethodMetadata(final Class<?> beanClass) {

        final List<AnnotatedMethodElement> elements = new LinkedList<AnnotatedMethodElement>();

        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {

                Method bridgedMethod = findBridgedMethod(method);

                if (!isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }

                Jcache annotation = findAnnotation(bridgedMethod, annotationType);

                if (annotation != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanClass))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("@" + annotationType.getSimpleName() + " annotation is not supported on static methods: " + method);
                        }
                        return;
                    }
                    if (method.getParameterTypes().length == 0) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("@" + annotationType.getSimpleName() + " annotation should only be used on methods with parameters: " +
                                    method);
                        }
                    }
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, beanClass);
                    elements.add(new JcacheAnnotationBeanPostProcessor.AnnotatedMethodElement(method, pd, annotation));
                }
            }
        });

        return elements;

    }

}

