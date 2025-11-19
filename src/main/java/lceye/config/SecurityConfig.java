package lceye.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        // 1. HTTP 요청에 따른 권한 커스텀
        httpSecurity.authorizeHttpRequests(auth -> auth
                // 회원 파트는 모든 요청에 대한 권한 허용
                .requestMatchers("/**").permitAll()
        );
        // 2. 차단된 csrf 차단 해체
        httpSecurity.csrf(csrf -> csrf.disable());

        // 3. 내가 만든 CorsConfig 설정
        httpSecurity.cors(Customizer.withDefaults());
        // 4. 커스텀 완료된 객체 반환
        return httpSecurity.build();
    } // func end
} // class end