package mtn.momo.contract.repayment.configs;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Configures and returns a RedisCacheManager bean.
     *
     * @param redisConnectionFactory A factory that allows for the creation of RedisConnection instances.
     * @return a fully configured RedisCacheManager instance.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    /**
     * Creates and configures a RedisTemplate for serializing and deserializing between Java objects and Redis stored data.
     *
     * @param redisConnectionFactory The factory to create connections to the Redis datastore.
     * @return A RedisTemplate configured for handling binary data.
     */
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
