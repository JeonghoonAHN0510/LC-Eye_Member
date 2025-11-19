package lceye.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lceye.model.dto.MemberDto;
import lceye.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * [MB-01] 로그인(login)
     * <p>
     * [아이디, 비밀번호]를 받아서 DB에 일치하는 회원이 존재한다면, Redis Session에 로그인 정보를 저장한다.
     * <p>
     * 테스트 : {"mid":"admin", "mpwd":"1234"}
     * @param memberDto 아이디, 비밀번호가 담긴 Dto
     * @param session 요청한 회원의 HTTP session 정보
     * @return 로그인을 성공한 회원의 Dto
     * @author AhnJH
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDto memberDto,
                                   HttpSession session){
        // 1. 입력받은 값을 Service에 전달하여 로그인 진행
        MemberDto result = memberService.login(memberDto);
        // 2. 로그인을 성공했다면, Redis Session에 정보 넣기
        if (result != null){
            result.setMpwd(null);
            session.setAttribute("loginMember", result);
        } // if end
        // 3. 최종적으로 결과 반환
        return ResponseEntity.ok(result);
    } // func end

    /**
     * [MB-02] 로그아웃(logout)
     * <p>
     * 요청한 회원의 로그인 정보를 Redis Session에서 제거한다.
     * @param session 요청한 회원의 HTTP 세션 정보
     * @return 로그아웃 성공 여부 - boolean
     * @author AhnJH
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session){
        // 1. Redis Session을 완전히 비우기
        session.invalidate();
        return ResponseEntity.ok(true);
    } // func end

    /**
     * [MB-03] 로그인 정보 확인(getInfo)
     * <p>
     * 요청한 회원의 [로그인 여부, 권한, 회원명, 회사명]을 반환한다.
     * @param session 요청한 회원의 HTTP 세션 정보
     * @return 요청한 회원의 정보
     * @author AhnJH
     */
    @GetMapping("/getinfo")
    public ResponseEntity<?> getInfo(HttpSession session){
        // 1. 세션에서 로그인 정보 꺼내기
        MemberDto memberDto = (MemberDto) session.getAttribute("loginMember");
        Map<String, Object> result = new HashMap<>();
        if (memberDto == null){
            // 2. 비로그인 상태라면 비로그인을 표시하고
            result.put("isAuth", false);
            // 3. HTTP 401으로 반환
            return ResponseEntity.status(401).body(result);
        } // if end
        // 4. 로그인 상태라면 로그인 정보를 구성하여 반환
        result.put("isAuth", true);
        result.put("role", memberDto.getMrole());
        result.put("cno", memberDto.getCno());
        result.put("cname", memberDto.getCname());
        result.put("mno", memberDto.getMno());
        result.put("mname", memberDto.getMname());
        return ResponseEntity.ok(result);
    } // func end
} // class end