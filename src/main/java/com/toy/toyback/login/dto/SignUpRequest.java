package com.toy.toyback.login.dto;

import com.toy.toyback.login.entity.SignUpEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String userId;
    private String password;
    private String userName;

    public SignUpEntity toEntity(String userId, String password, String userName) {
        return new SignUpEntity(
                this.userId, this.password, this.userName);
    };
}
