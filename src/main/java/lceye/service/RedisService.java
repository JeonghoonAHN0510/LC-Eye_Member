package lceye.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.Optional;

import jakarta.transaction.Transactional;
import lceye.model.dto.MemberDto;
import lceye.model.dto.RedisRequestDto;
import lceye.model.dto.RedisResponseDto;
import lceye.model.entity.MemberEntity;
import lceye.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RedisService implements MessageListener {
    private final RedisTemplate<String, String> redisStringTemplate;
    private final MemberRepository memberRepository;
    private final ChannelTopic projectTopic;       // 응답을 보낼 채널
    private final ObjectMapper objectMapper;        // JSON 변환을 위해

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. 받은 메시지를 문자열로 변환하고 mno 추출
            String body = new String(message.getBody());
            RedisRequestDto requestDto = objectMapper.readValue(body, RedisRequestDto.class);
            // 2. mno로 MemberEntity 추출
            int mno = requestDto.getMno();
            Optional<MemberEntity> memberEntity = memberRepository.findById(mno);
            if (memberEntity.isPresent()){
                MemberEntity member = memberEntity.get();
                // 3. MemberEntity를 MemberDto로 변환
                MemberDto memberDto = member.toDto();
                memberDto.setCno(member.getCompanyEntity().getCno());
                // 4. 응답 객체 생성
                RedisResponseDto responseDto = RedisResponseDto.builder()
                        .responseId(requestDto.getRequestId())
                        .responseMember(memberDto)
                        .build();
                // 5. 응답 객체를 JSON 문자열로 변환
                String jsonResult = objectMapper.writeValueAsString(responseDto);
                // 6. 8081 서버로 발행
                redisStringTemplate.convertAndSend(projectTopic.getTopic(), jsonResult);
            } // if end
        } catch (Exception e) {
            log.error(e.getMessage());
        } // try-catch end
    } // func end
} // class end