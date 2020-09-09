package org.rrx.jcache.commons.redis;

import org.rrx.jcache.commons.config.properties.JedisProperties;
import org.rrx.jcache.commons.config.properties.RedisProperties;
import org.rrx.jcache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;

import java.util.Set;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/27 18:35
 * @Description:
 */
public class RedisClients {

    private static final Logger log = LogFactory.getLogger(RedisClients.class);

    private RedisProperties redisProperties;

    private JedisProperties jedisProperties;

    private JedisPool jedisPool;

    public RedisClients(RedisProperties redisProperties, JedisProperties jedisProperties) {
        this.redisProperties = redisProperties;
        this.jedisProperties = jedisProperties;
    }

    public void initClients() {

        if (StringUtils.isEmpty(redisProperties.getPassword())) {
            this.jedisPool = new JedisPool(getPoolConfig(), redisProperties.getHost(),
                    redisProperties.getPort(), Protocol.DEFAULT_TIMEOUT);
        } else {
            this.jedisPool = new JedisPool(getPoolConfig(), redisProperties.getHost(),
                    redisProperties.getPort(), Protocol.DEFAULT_TIMEOUT,
                    redisProperties.getPassword(), redisProperties.getDatabase(),
                    null);
        }
        log.info("RedisClients start success");
    }

    public void close() {
        if (this.jedisPool != null) {
            this.jedisPool.close();
        }
    }

    public String makeKey(String key) {
        return redisProperties.getPrefix() + key;
    }


    public String get(String key) {
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
        poolConfig.setTestOnBorrow(jedisProperties.isTestOnBorrow());
        poolConfig.setMaxIdle(jedisProperties.getMaxIdle());
        poolConfig.setMinIdle(jedisProperties.getMinIdle());
        poolConfig.setMinEvictableIdleTimeMillis(jedisProperties.getSoftMinEvictableIdleTime());
        poolConfig.setMaxWaitMillis(jedisProperties.getMaxWaitMillis());
        poolConfig.setMaxTotal(jedisProperties.getMaxTotal());
        return poolConfig;
    }
}
