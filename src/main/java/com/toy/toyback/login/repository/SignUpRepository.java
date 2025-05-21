package com.toy.toyback.login.repository;

import com.toy.toyback.login.entity.SignUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SignUpRepository extends JpaRepository<SignUpEntity, String> {
    @Modifying
    @Transactional
    @Query("INSERT INTO SignUpEntity (id, password, name) " +
            "VALUES (:userId, :password, :userName)")
    Integer signUp(@Param("userId") String userId,@Param("password") String password,@Param("userName") String userName);

}
