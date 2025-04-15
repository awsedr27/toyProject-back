package com.toy.toyback.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "app_entity")
public class AppEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true)
    private String clientId;

    private String clientPassword;

    private String redirectUri;
}
