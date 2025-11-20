package lceye.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService implements MessageListener {
    private final MemberService memberService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        memberService.getMember(Integer.parseInt(body));
    } // func end
} // class end