package com.dz.coop.conf;

import com.dz.glory.common.jedis.client.JedisClient;
import com.dz.glory.common.jedis.sharding.Ketama2PoolManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConf {

    @Value("${redis.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${redis.maxTotal}")
    private int maxTotal;

    @Value("${redis.maxWait}")
    private int maxWait;

    @Value("${redis.maxIdle}")
    private int maxIdle;

    @Value("${redis.minIdle}")
    private int minIdle;

    @Value("${redis.pass}")
    private String pass;

    @Value("${redis.servers}")
    private String servers;

    /**
     * 业务数据
     * @return jedis
     */
    @Bean
    @Lazy(false)
    public JedisClient jedisClient() {
        return getJedisClient(servers);
    }

    @Bean
    @Lazy(false)
    public com.dz.jedis.client.JedisClient newJedisClient() {
        return getNewJedisClient(servers);
    }

    private JedisClient getJedisClient(String servers) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        setJedisPoolConfig(jedisPoolConfig);

        Ketama2PoolManagerFactory poolManagerFactory = new Ketama2PoolManagerFactory(servers, pass, jedisPoolConfig);
        JedisClient jedisClient = new JedisClient();
        jedisClient.setJedisPoolManagerFactory(poolManagerFactory);
        return jedisClient;
    }

    private com.dz.jedis.client.JedisClient getNewJedisClient(String servers) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        setJedisPoolConfig(jedisPoolConfig);

        com.dz.jedis.sharding.Ketama2PoolManagerFactory poolManagerFactory = new com.dz.jedis.sharding.Ketama2PoolManagerFactory(servers, pass, jedisPoolConfig);
        com.dz.jedis.client.JedisClient jedisClient = new com.dz.jedis.client.JedisClient();
        jedisClient.setJedisPoolManagerFactory(poolManagerFactory);
        return jedisClient;
    }

    private void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(3600000);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(300000);
    }

}
