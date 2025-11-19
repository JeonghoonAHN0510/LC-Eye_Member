package lceye.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lceye.model.dto.MemberDto;
import lceye.model.entity.CompanyEntity;
import lceye.model.entity.MemberEntity;
import lceye.model.mapper.MemberMapper;
import lceye.model.repository.CompanyRepository;
import lceye.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final MemberMapper memberMapper;

    /**
     * [MB-01] 로그인(login)
     * <p>
     * [아이디, 비밀번호]를 받아서 DB에 일치하는 회원이 존재한다면, Redis와 Cookie에 로그인 정보가 담긴 JWT 토큰을 저장한다.
     * </p>
     * @param memberDto 아이디, 비밀번호가 담긴 Dto
     * @return 로그인을 성공한 회원의 Dto
     * @author AhnJH
     */
    public MemberDto login(MemberDto memberDto){
        // 1. 요청된 아이디와 비밀번호가 유효한지 확인하여 반환
        return memberMapper.login(memberDto);
    } // func end


    public MemberDto getMemberDtoById(int mno){
        MemberEntity memberEntity = memberRepository.getReferenceById(mno);
        System.out.println("memberEntity = " + memberEntity);
        System.out.println("memberEntity.toDto() = " + memberEntity.toDto());
        return memberEntity.toDto();
    } // func end
} // class end