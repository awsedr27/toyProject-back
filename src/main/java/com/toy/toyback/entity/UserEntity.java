package com.toy.toyback.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TOY_USER_ENTITY")
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true)
    private String clientId;

    private String clientSecret;

    private String redirectUri;
}
