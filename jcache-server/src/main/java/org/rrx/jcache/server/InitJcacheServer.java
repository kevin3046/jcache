package org.rrx.jcache.server;

import org.rrx.jcache.commons.config.properties.JcacheServerConfigProperties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/22 16:56
 * @Description:
 */
public class InitJcacheServer implements ApplicationListener, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private JcacheServerConfigProperties configProperties;

    private JcacheServer jcacheServer;

    // 启动标记：0-未初始化；1-进行中；2-初始化结束
    private volatile InitStage hasInited = InitStage.INIT;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if (applicationEvent instanceof ContextRefreshedEvent) {
            init();
        } else if (applicationEvent instanceof ContextStoppedEvent) {
            destroy();
        }
    }

    private void init() {

        this.configProperties = (JcacheServerConfigProperties) applicationContext.getBean(JcacheServerConfigProperties.class.getName());

        if (hasInited != InitStage.INIT) {
            return;
        }

        // 设置标记位
        hasInited = InitStage.ING;

        initPool();

        hasInited = InitStage.DONE;
    }

    private void destroy() {
        if (jcacheServer != null) {
            jcacheServer.stop();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void initPool() {
        //初始化客户端各项连接
        this.jcacheServer = new JcacheServer(configProperties);
        this.jcacheServer.startServer();
    }

    public enum InitStage {
        INIT, ING, DONE
    }
}
