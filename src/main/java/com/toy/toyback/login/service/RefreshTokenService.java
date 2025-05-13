package com.toy.toyback.login.service;

import com.toy.toyback.login.entity.RefreshTokenEntity;
import com.toy.toyback.jwt.JwtProvider;
import com.toy.toyback.login.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProvider jwtProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public RefreshTokenEntity validateAndUpdateRefreshToken(String originRefreshToken) throws Exception{
        //리프레쉬토큰 검증
        String userId = jwtProvider.getSubject(originRefreshToken);

        //  DB에서 해당 유저의 리프레시 토큰 조회
        RefreshTokenEntity storedRefreshToken = refreshTokenRepository.findByUserEntity_UserId(userId)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 등록되지 않았습니다."));

        if(!storedRefreshToken.getRefreshToken().equals(originRefreshToken)) {
            //1회이상 사용된 토큰, exception
            new RuntimeException("잘못된 토큰 사용입니다.");
        }

        //  새로운 리프레시 토큰 생성
        String newRefreshToken = jwtProvider.generateRefreshToken(storedRefreshToken.getUserEntity().getUserId());

        //  기존 리프레시 토큰을 새 토큰으로 업데이트
        storedRefreshToken.setRefreshToken(newRefreshToken);

        //  업데이트된 리프레시 토큰을 DB에 저장
        refreshTokenRepository.save(storedRefreshToken);

        //  업데이트된 리프레시 토큰 객체 반환
        return storedRefreshToken;
    }
}