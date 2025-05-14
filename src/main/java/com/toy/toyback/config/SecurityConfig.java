package com.toy.toyback.config;

import com.toy.toyback.jwt.JwtAuthFilter;
import com.toy.toyback.login.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService,JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws  Exception{

        // csrf disable
        httpSecurity
                .csrf((auth) -> auth.disable());

        // form login 방식 disable
        httpSecurity
                .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable
        httpSecurity
                .httpBasic((auth) -> auth.disable());

        // 경로 인가 작업
        httpSecurity
                .authorizeHttpRequests((auth -> auth
                        .requestMatchers("/users/login","/users/refresh", "/", "/auth/**", "/join","/error","/users/test").permitAll() //나중에 정리 필요
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()));

        // session 설정
        // JWT는 서버가 인증 상태를 기억하지 않고, 클라이언트가 매번 토큰을 보내야 하니까 session을 Sateless 처리
/*        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));*/

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
        return builder.build();
    }
}
