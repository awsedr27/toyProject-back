package com.toy.toyback.login.dto;

import java.time.LocalDateTime;

import com.toy.toyback.login.entity.SignUpEntity;
import com.toy.toyback.login.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String userId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean used;

    public UserEntity toEntity() {
        return new UserEntity(
            this.userId,
            this.password,
            this.name,
            this.email,
            this.phoneNumber,
            this.used,
            LocalDateTime.now(), // 생성일
            LocalDateTime.now()  // 수정일
        );
    }
}
