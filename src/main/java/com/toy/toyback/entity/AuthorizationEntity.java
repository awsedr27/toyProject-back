package com.toy.toyback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AuthorizationEntity {
    @Id
    private String code;

    private String clientId;
    private String redirectUri;

    private boolean used;
    private LocalDateTime createdAt;

}