package org.rrx.jcache.server;

import org.rrx.jcache.commons.config.properties.JcacheServerConfigProperties;
import org.rrx.jcache.commons.dto.MessageBean;
import org.rrx.jcache.commons.etcd.EtcdClients;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.commons.redis.RedisClients;
import org.rrx.jcache.commons.rocketmq.RocketConsumer;
import org.rrx.jcache.server.election.SchedulerLeader;
import org.rrx.jcache.server.hots.HotsService;
import org.rrx.jcache.server.hots.SchedulerHotsConverge;
import org.rrx.jcache.server.hots.SchedulerHotsDetect;
import org.rrx.jcache.server.listener.KeyReportListener;
import org.rrx.setcd.commons.clients.SetcdClients;
import org.slf4j.Logger;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/25 19:24
 * @Description:
 */
public class JcacheServer {

    private static final Logger log = LogFactory.getLogger(JcacheServer.class);

    private JcacheServerConfigProperties configProperties;

    private RedisClients redisClients;

    private HotsService hotsService;

    private SetcdClients setcdClients;

    public JcacheServer(JcacheServerConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public void startServer() {
        //初始化jedis连接池
        redisClients = new RedisClients(configProperties.getRedisProperties(), configProperties.getJedisProperties());
        redisClients.initClients();

        //初始化etcd连接
        EtcdClients etcdClients = new EtcdClients(configProperties.getEtcdProperties());
        this.setcdClients = etcdClients.setcdClientsBuild();

        hotsService = new HotsService(redisClients, setcdClients);

        RocketConsumer consumer = new RocketConsumer(configProperties.getRocketmqProperties());

        KeyReportListener listener = new KeyReportListener(hotsService);
        listener.setTopicName(configProperties.getRocketmqProperties().getTopicKeyReport());
        listener.setTagName(configProperties.getRocketmqProperties().getTopicTagsReport());
        listener.setClazz(MessageBean.class);
        consumer.registerListener(listener);

        //启动热点汇聚线程
        SchedulerHotsConverge hotsConverge = new SchedulerHotsConverge(hotsService);
        hotsConverge.startAsync();
        log.info("SchedulerHotsConverge start success");

        //启动热点探测线程
        SchedulerHotsDetect hotsDetect = new SchedulerHotsDetect(hotsService);
        hotsDetect.startAsync();
        log.info("SchedulerHotsDetect start success");

        //启动选举线程
        SchedulerLeader leader = new SchedulerLeader();
        leader.startAsync();
        log.info("SchedulerLeader start success");

        log.info("JcacheServer start success");
    }

    public void stop() {

    }
}
