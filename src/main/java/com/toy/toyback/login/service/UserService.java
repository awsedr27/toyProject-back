package com.toy.toyback.login.service;

import com.toy.toyback.login.dto.LoginDto;
import com.toy.toyback.login.dto.LoginRequest;
import com.toy.toyback.code.AppRole;
import com.toy.toyback.login.entity.RefreshTokenEntity;
import com.toy.toyback.jwt.JwtProvider;
import com.toy.toyback.login.entity.SignUpEntity;
import com.toy.toyback.login.repository.RefreshTokenRepository;
import com.toy.toyback.login.repository.SignUpRepository;
import com.toy.toyback.login.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {


    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SignUpRepository signUpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AuthenticationManager authenticationManager, JwtProvider jwtProvider, RefreshTokenRepository refreshTokenRepository, SignUpRepository signUpRepository
            , UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.signUpRepository = signUpRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginDto.TokenResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserId(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails principal = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(principal.getUsername(), AppRole.ROLE_USER);
        String refreshToken = jwtProvider.generateRefreshToken(principal.getUsername());

        refreshTokenRepository.findByUserEntity_UserId(principal.getUsername()).ifPresentOrElse(
                existingToken -> {
                    existingToken.setRefreshToken(refreshToken);
                    existingToken.setUpdatedAt(LocalDateTime.now());
                    refreshTokenRepository.save(existingToken);
                },
                () -> {
                    RefreshTokenEntity newToken = new RefreshTokenEntity();
                    newToken.setUserEntity(userRepository.findByUserId(principal.getUsername())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found for userId: " + principal.getUsername())));
                    newToken.setRefreshToken(refreshToken);
                    newToken.setCreatedAt(LocalDateTime.now());
                    newToken.setUpdatedAt(LocalDateTime.now());
                    refreshTokenRepository.save(newToken);
                }
        );
        return new LoginDto.TokenResponse(accessToken, refreshToken);
    }

    public String SignUp(SignUpEntity signUpEntity) {
        String userPassword = signUpEntity.getPassword();
        String encodedPassword = passwordEncoder.encode(userPassword);

        Integer result = signUpRepository.signUp(
                signUpEntity.getId(),
                encodedPassword,
                signUpEntity.getName()
        );

        return result > 0 ? "회원가입 성공" : "회원가입 실패";
    }
}
