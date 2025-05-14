package com.toy.toyback.login.controller;

import com.toy.toyback.login.dto.LoginDto;
import com.toy.toyback.login.dto.LoginRequest;
import com.toy.toyback.login.dto.LoginResponse;
import com.toy.toyback.code.AppRole;
import com.toy.toyback.login.entity.RefreshTokenEntity;
import com.toy.toyback.jwt.JwtProvider;
import com.toy.toyback.login.service.RefreshTokenService;
import com.toy.toyback.login.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.Duration;

@RestController
@RequestMapping("/users")
public class UserController {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserController(UserService userService, JwtProvider jwtProvider, RefreshTokenService refreshTokenService,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    
    @PostMapping("/test")
    public ResponseEntity<LoginResponse> login() {
        return ResponseEntity.ok()
                .body(new LoginResponse(bCryptPasswordEncoder.encode("test").toString()));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            LoginDto.TokenResponse rs = userService.login(loginRequest);
            // 응답 헤더에 JWT 토큰 포함
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + rs.getAccessToken());
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", rs.getRefreshToken())
                    .httpOnly(true)            // JS에서 접근 불가
                    //.secure(true)              // HTTPS 환경에서만 전송
                    .path("/users/refresh") // 사용할 경로 제한 (선택)
                    .maxAge(Duration.ofDays(7))
                    //.sameSite("Strict")        // CSRF 보호 강화
                    .build();
            // 인증 성공 응답 헤더 반환(refresh token)
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
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
    public ResponseEntity<LoginResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // 세션 무효화
        }
        return ResponseEntity.ok(new LoginResponse("로그아웃 성공"));
    }

    // refresh Access Token By using refreshToken
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        try{

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        String originRefreshToken=cookie.getValue();
                        //리프레쉬 토큰 검증 및 재발급 서비스
                        RefreshTokenEntity newRefreshToken=refreshTokenService.validateAndUpdateRefreshToken(originRefreshToken);

                        //엑세스토큰 재발급
                        String jwtToken=jwtProvider.generateAccessToken(newRefreshToken.getUserEntity().getUserId(), AppRole.ROLE_USER);

                        // 기존 리프레시 쿠키 삭제 (만료시킴)
                        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                                .httpOnly(true)            // JS에서 접근 불가
                                //.secure(true)              // HTTPS 환경에서만 전송
                                .path("/users/refresh") // 사용할 경로 제한 (선택)
                                .maxAge(0)  //즉시 만료
                                //.sameSite("Strict")        // CSRF 보호 강화
                                .build();

                        //액세서토큰 , 리프레쉬토큰 보내줌
                        // 응답 헤더에 JWT 토큰 포함
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", "Bearer " + jwtToken);

                        // 인증 성공 응답 헤더 반환(refresh token)

                        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken.getRefreshToken())
                                .httpOnly(true)            // JS에서 접근 불가
                                //.secure(true)              // HTTPS 환경에서만 전송
                                .path("/users/refresh") // 사용할 경로 제한 (선택)
                                .maxAge(Duration.ofDays(7))
                                //.sameSite("Strict")        // CSRF 보호 강화
                                .build();
                        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                        // 인증 성공 응답 반환 (JWT 포함)
                        return ResponseEntity.ok()
                                .headers(headers)
                                .body(new LoginResponse("재발급 성공"));
                    }
                }
            }
            //exception 토큰없음
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("로그인 실패"));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("로그인 실패"));
        }
    }

}

