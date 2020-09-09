package org.rrx.jcache.tests.annotation.clients;

import org.rrx.jcache.clients.config.spring.annotation.Jcache;
import org.rrx.jcache.clients.utils.StringUtils;
import org.rrx.jcache.commons.dto.Operate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import java.util.Set;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/27 18:35
 * @Description:
 */
@Component
public class RedisClientsTest implements InitializingBean, DisposableBean {

    @Value(value = "${redis.host}")
    private String host;

    @Value(value = "${redis.port}")
    private Integer port;

    @Value(value = "${redis.password}")
    private String password;

    @Value(value = "${redis.database}")
    private Integer database = 0;

    @Value(value = "${redis.prefix}")
    private String prefix;


    @Value(value = "${jedis.pool.testOnBorrow}")
    private Boolean testOnBorrow;

    @Value(value = "${jedis.pool.minIdle}")
    private Integer minIdle;

    @Value(value = "${jedis.pool.maxIdle}")
    private Integer maxIdle;

    @Value(value = "${jedis.pool.softMinEvictableIdleTime}")
    private Integer softMinEvictableIdleTime;

    @Value(value = "${jedis.pool.maxTotal}")
    private Integer maxTotal;

    @Value(value = "${jedis.pool.maxWaitMillis}")
    private Integer maxWaitMillis;


    private JedisPool jedisPool;

    public RedisClientsTest() {

    }

    @Override
    public void destroy() throws Exception {
        this.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initClients();
    }

    public void initClients() {
        if (StringUtils.isEmpty(this.password)) {
            this.jedisPool = new JedisPool(getPoolConfig(), this.host,
                    port, Protocol.DEFAULT_TIMEOUT);
        } else {
            this.jedisPool = new JedisPool(getPoolConfig(), this.host,
                    port, Protocol.DEFAULT_TIMEOUT,
                    this.password, this.database,
                    null);
        }
    }

    public void close() {
        if (this.jedisPool != null) {
            this.jedisPool.close();
        }
    }

    public String makeKey(String key) {
        return this.prefix + key;
    }

    @Jcache(operate = Operate.GET)
    public String get(String key) {
        if (key == null) {
            return "";
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(makeKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String getNoPrefix(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long ttlNoPrefix(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Jcache(operate = Operate.SET)
    public String set(String key, String value) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(makeKey(key), value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Jcache(operate = Operate.SET)
    public Long expire(String key, int expire) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.expire(makeKey(key), expire);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Jcache(operate = Operate.DELETE)
    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(makeKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long zadd(String key, Long score, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zadd(makeKey(key), score.doubleValue(), member);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long zincrBy(String key, Long score, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Double ret = jedis.zincrby(makeKey(key), score.doubleValue(), member);
            return ret.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrevrangeWithScores(makeKey(key), start, end);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.incr(makeKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private JedisPoolConfig getPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMinEvictableIdleTimeMillis(softMinEvictableIdleTime);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxTotal(maxTotal);
        return poolConfig;
    }
}
