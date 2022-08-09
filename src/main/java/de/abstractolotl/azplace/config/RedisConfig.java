package de.abstractolotl.azplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisUrl;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(redisUrl);
        config.setPort(redisPort);

        if(!redisPassword.isEmpty())
            config.setPassword(RedisPassword.of(redisPassword));

        return config;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(redisStandaloneConfiguration());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> myRedisTemplate = new RedisTemplate<>();
        myRedisTemplate.setConnectionFactory(jedisConnectionFactory());
        return myRedisTemplate;
    }

    @Bean
    public Jedis jedis() {
        return ((JedisConnection) jedisConnectionFactory().getConnection()).getJedis();
    }

}
