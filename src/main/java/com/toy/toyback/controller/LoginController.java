package com.toy.toyback.controller;

import com.toy.toyback.entity.UserEntity;
import com.toy.toyback.jwt.JwtProvider;
import com.toy.toyback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public String login(@RequestBody UserEntity request) {

        UserEntity user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String token = jwtProvider.generateAccessTokenWithRole(user.getUserName(), user.getUserRole());

        return token;
    }
}
