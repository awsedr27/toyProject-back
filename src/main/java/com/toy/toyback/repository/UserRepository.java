package com.toy.toyback.repository;

import com.toy.toyback.entity.AppEntity;
import com.toy.toyback.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {


    Optional<UserEntity> findByUserId(String userId);

}
