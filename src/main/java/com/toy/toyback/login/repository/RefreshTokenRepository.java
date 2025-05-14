package com.toy.toyback.login.repository;

import com.toy.toyback.login.entity.RefreshTokenEntity;
import com.toy.toyback.login.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    // user_id로 리프레시 토큰을 조회하는 메소드
    Optional<RefreshTokenEntity> findByUserEntity_UserId(String userId);

//    // 리프레시 토큰을 삭제하는 메소드 (필요 시 추가)
//    void deleteByUserId(Long userId);
//
//    // 필요에 따라 리프레시 토큰의 만료일 기준으로 삭제하는 메소드도 추가 가능
//    void deleteByExpiredAtBefore(java.time.LocalDateTime expiredAt);

    // user_id에 해당하는 리프레시 토큰을 삽입하는 쿼리
//    @Modifying
//    @Transactional
//    @Query("INSERT INTO RefreshToken (userEntity, refreshToken, createdAt,updatedAt) " +
//            "VALUES (:userEntity, :refreshToken, :createdAt, :updatedAt)")
//    void insertRefreshToken(UserEntity userEntity, String refreshToken, LocalDateTime createdAt, LocalDateTime updatedAt);


}
