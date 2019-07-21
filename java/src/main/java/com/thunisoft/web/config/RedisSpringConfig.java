package com.thunisoft.web.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;
/**
 * @Author: Diodeme
 * @Date: 2019/5/15
 */

/**
 * redis配置类
 */
@Configuration
@PropertySource(value = "classpath:redis.properties")//引入配置
public class RedisSpringConfig {

    @Value("${redis.maxTotal}")
    private Integer redisMaxTotal;

    @Value("${redis.node.host}")
    private String redisNodeHost;

    @Value("${redis.node.port}")
    private Integer redisNodePort;

    @Value("${redis.timeout}")
    private int timeout;

    @Value("${redis.password}")
    private String password;

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisMaxTotal);
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool getJedisPool(){    // 省略第一个参数则是采用 Protocol.DEFAULT_DATABASE
        JedisPool jedisPool = new JedisPool(jedisPoolConfig(), redisNodeHost, redisNodePort,timeout,password);
        return jedisPool;
    }

    @Bean
    public ShardedJedisPool shardedJedisPool() {
        List<JedisShardInfo> jedisShardInfos = new ArrayList<JedisShardInfo>();
        jedisShardInfos.add(new JedisShardInfo(redisNodeHost, redisNodePort));
        return new ShardedJedisPool(jedisPoolConfig(), jedisShardInfos);
    }
}