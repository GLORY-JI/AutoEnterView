package com.ctrls.auto_enter_view.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Bean
  public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

    RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new StringRedisSerializer()));

    return RedisCacheManager.RedisCacheManagerBuilder
        .fromConnectionFactory(redisConnectionFactory)
        .cacheDefaults(conf)
        .build();
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {

    RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
    conf.setHostName(this.host);
    conf.setPort(this.port);

    return new LettuceConnectionFactory(conf);
  }

  @Bean
  public RedisTemplate<String, String> redisStringTemplate(
      RedisConnectionFactory redisConnectionFactory) {

    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());

    return template;
  }

  @Bean
  public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {

    GenericJackson2JsonRedisSerializer jsonRedisSerializer =
        new GenericJackson2JsonRedisSerializer("com.ctrls.auto_enter_view.dto.common.MainJobPostingDto.Response");

    jsonRedisSerializer.configure(objectMapper -> {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    });

    return jsonRedisSerializer;
  }

  @Bean
  public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory redisConnectionFactory,
      GenericJackson2JsonRedisSerializer jsonRedisSerializer) {

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(jsonRedisSerializer);

    return template;
  }
}