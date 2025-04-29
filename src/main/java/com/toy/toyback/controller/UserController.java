package com.toy.toyback.controller;

import com.toy.toyback.dto.LoginRequest;
import com.toy.toyback.dto.LoginResponse;
import com.toy.toyback.entity.AppRole;
import com.toy.toyback.jwt.JwtProvider;
import com.toy.toyback.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")
public class UserController {


    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    public UserController(AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder= bCryptPasswordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("test")
    public ResponseEntity<LoginResponse> test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(new LoginResponse(authentication.getName()));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserId(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 인증된 사용자 정보에서 username 추출
            UserDetails principal =(UserDetails)authentication.getPrincipal();
            String jwtToken=jwtProvider.generateAccessToken(principal.getUsername(), AppRole.ROLE_USER);
            // 응답 헤더에 JWT 토큰 포함
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);

            // 인증 성공 응답 반환 (JWT 포함)
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new LoginResponse("로그인 성공"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("로그인 실패"));
        }

    }

    // 로그아웃 엔드포인트
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // 세션 무효화
        }
        return ResponseEntity.ok("로그아웃 성공");
    }
}

