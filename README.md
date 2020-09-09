# kevin3046 jcache project
###项目介绍
jcache Redis热点key解决方案

git地址：https://github.com/kevin3046/jcache

作者邮箱：kevin3046@163.com

### 快速入门

#### 1.环境准备
- 安装redis单实例/集群,本文使用 redis 3.0.4 版本
- 安装etcd单实例/集群,本文使用 etcd 3.4.12 版本
- 安装rocketmq单实例/集群,本文使用 rocketmq 4.7.1 版本

#### 2. 应用ClientApp侧

#### 2.1 Maven配置
```xml
<dependency>
    <groupId>org.rrx.jcache</groupId>
    <artifactId>jcache-clients</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 2.2 jcache.properties配置
```bash
#app名称
jcache.base.appname=jcache_client_demo
#项目使用的redis前缀
jcache.base.redisPrefix=jcache_client_demo:
#需要维持的redis热点key最大个数
jcache.base.hotsTopSize=100
#热点key的阀值,同一appname下所以的key共享
jcache.base.hotsThreshold=10
#etcd配置,如果为集群配置,请使用逗号分隔服务器地址
jcache.etcd.serverAddr=http://etcd.dev-test.com:2379
#etcd命名空间
jcache.etcd.namespace=/jcache
#rocketmq配置
jcache.rocketmq.namesrvAddr=rocketmq.dev-test.com:9876
```

#### 2.3 全局注解@EnableJcacheClients
```java
@Configuration
@EnableJcacheClients
@PropertySource("classpath:jcache.properties")
@PropertySource("classpath:redis.properties")
public class ClientsConfig {

}
```
1、 @EnableJcacheClients 开启jcache

2、 @PropertySource("classpath:jcache.properties") 加载jcache的配置文件

#### 2.4 热点缓存注解@Jcache使用

1、@Jcache(operate = Operate.GET) 修饰某个redis获取key值的方法

1.1、被该注解修饰的方法，第一个参数需要为String类型的key键

1.2、jcache将尝试从本地缓存中获取key对应的value值

2、@Jcache(operate = Operate.SET),@Jcache(operate = Operate.DELETE),@Jcache(operate = Operate.EXPIRE)

2.1、被该注解修饰的方法，第一个参数需要为String类型的key键

2.2、jcache将删除本地缓存中key对应的value值

#### 2.5 jcache-test-client 示例

1、本项目下，建立了两个client端的节点，分别为：jcache-tests-client-8004，8005

2、这两个节点，基于spring-boot进行构建，模拟同一应用，多机部署的情况。


#### 3. Jcache Server侧

#### 3.1 Maven配置
```xml
<dependency>
    <groupId>org.rrx.jcache</groupId>
    <artifactId>jcache-server</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 3.2 jcache.properties配置
```bash
#server的项目名称
jcache.appname=jcache_server
#redis连接配置
jcache.redis.host=redis.dev-test.com
jcache.redis.port=6379
jcache.redis.password=
jcache.redis.database=0
#jcache连接池配置
jcache.redis.prefix=jcache_server:
jcache.jedis.pool.testOnBorrow=true
jcache.jedis.pool.minIdle=8
jcache.jedis.pool.maxIdle=64
jcache.jedis.pool.softMinEvictableIdleTime=300000
jcache.jedis.pool.maxTotal=512
jcache.jedis.pool.maxWaitMillis=1000
#etcd配置
jcache.etcd.serverAddr=http://etcd.dev-test.com:2379
jcache.etcd.namespace=/jcache
#rocketmq配置
#生产者组名
jcache.rocketmq.producerGroupName=producer_jcache
#消费者组名
jcache.rocketmq.consumerGroupName=consumer_jcache
#rocketmq nameserver地址
jcache.rocketmq.namesrvAddr=rocketmq.dev-test.com:9876
#发送消息超时时间
jcache.rocketmq.sendMsgTimeout=3000
#失败重试次数
jcache.rocketmq.retryTimesWhenSendFailed=2
#热点key上报topic
jcache.rocketmq.topicKeyReport=T_KEY_REPORT
#热点key上报tag
jcache.rocketmq.topicTagsReport=T_KEY_REPORT
```

#### 2.3 全局注解@EnableJcacheServer
```java
@Configuration
@EnableJcacheServer
@PropertySource("classpath:jcache.properties")
public class ServerConfig {

}
```

1、@EnableJcacheServer 开始jcache server端

2、@PropertySource("classpath:jcache.properties") 加载配置文件

#### 2.3 jcache-tests-server 示例

1、本项目下，建立了3个server节点，组成server端集群，分别为：jcache-tests-server9004，9005,9006

2、这3个节点，基于spring-boot进行构建，组成一个3节点的集群，基于etcd进行选举。


