package lceye.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lceye.model.dto.MemberDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService implements MessageListener {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. 받은 메시지를 문자열로 변환
            String body = new String(message.getBody());

            System.out.println("[8080 서버] 요청 받음! ");

            System.out.println("[8080 서버] 응답 보냄: ");
        } catch (Exception e) {
            System.out.println("e = " + e);
        } // try-catch end
    } // func end
} // class end