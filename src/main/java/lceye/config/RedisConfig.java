package lceye.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lceye.model.dto.MemberDto;
import lceye.service.RedisService;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisJSONTemplate(RedisConnectionFactory redisConnectionFactory){
        // 1. Redis 템플릿 객체 생성 : Redis 형식을 Map 타입으로 사용하기위한 설정
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 2. 생성한 템플릿 객체를 팩토리(Redis 저장소)에 등록
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 3. 생성한 템플릿은 key값을 String 타입으로 직렬화한다.
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 4. 생성한 템플릿은 value값을 JSON/DTO 타입으로 직렬화한다,
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 직렬화 : Redis에 저장된 데이터를 자바 타입으로 변환 과정
        return redisTemplate;
    } // func end

    @Bean
    public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory redisConnectionFactory){
        // 1. Redis 템플릿 객체 생성 : Redis 형식을 Map 타입으로 사용하기위한 설정
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        // 2. 생성한 템플릿 객체를 팩토리(Redis 저장소)에 등록
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 3. 생성한 템플릿은 key값을 String 타입으로 직렬화한다.
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 4. 생성한 템플릿은 value값을 String 타입으로 직렬화한다,
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        // 직렬화 : Redis에 저장된 데이터를 자바 타입으로 변환 과정
        return redisTemplate;
    } // func end

    // 2. 통신할 채널 정의
    @Bean
    public ChannelTopic topic(){
        return new ChannelTopic("8080server");
    } // func end

    // 3. 컨테이너 설정
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        RedisService redisService,
                                                        ChannelTopic topic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);      // Redis 연결 정보 설정
        container.addMessageListener(redisService, topic);   // "8080server"에 오면 listenerAdapter 실행
        return container;
    } // func end
} // class end