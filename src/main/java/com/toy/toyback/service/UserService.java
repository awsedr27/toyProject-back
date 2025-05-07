package com.toy.toyback.service;

import com.toy.toyback.dto.LoginDto;
import com.toy.toyback.dto.LoginRequest;
import com.toy.toyback.entity.AppRole;
import com.toy.toyback.entity.RefreshToken;
import com.toy.toyback.jwt.JwtProvider;
import com.toy.toyback.repository.RefreshTokenRepository;
import com.toy.toyback.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {


    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public UserService(AuthenticationManager authenticationManager, JwtProvider jwtProvider, RefreshTokenRepository refreshTokenRepository
    , UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
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
                    RefreshToken newToken = new RefreshToken();
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

}
