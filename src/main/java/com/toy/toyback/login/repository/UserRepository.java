package com.toy.toyback.login.repository;

import com.toy.toyback.login.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
