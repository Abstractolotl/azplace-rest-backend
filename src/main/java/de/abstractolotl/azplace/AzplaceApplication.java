package de.abstractolotl.azplace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

@SpringBootApplication
public class AzplaceApplication {

    @Value("${app.redis.url}")
    private String redisUrl;
    @Value("${app.redis.port}")
    private int    redisPort;
    @Value("${app.redis.userName}")
    private String redisUserName;
    @Value("${app.redis.password}")
    private String redisPassword;

    @Bean
    public Jedis jedis() {
        if (redisUserName.isBlank() || redisPassword.isBlank()) {
            return new Jedis(redisUrl, redisPort);
        }
        HostAndPort hostAndPort = new HostAndPort(redisUrl, redisPort);
        DefaultJedisClientConfig jedisClientConfiguration = DefaultJedisClientConfig
                .builder()
                .user(redisUserName)
                .password(redisPassword)
                .build();
        return new Jedis(hostAndPort, jedisClientConfiguration);
    }

    public static void main(String[] args) {
        SpringApplication.run(AzplaceApplication.class, args);
    }

}