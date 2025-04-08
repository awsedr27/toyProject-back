package com.toy.toyback.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "TOY_USER_ENTITY")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userId;
    private String userName;
    private String password;
    private String email;
    private String mobileNo;
    private String userStatus;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Date createdAt;
    private Date updatedAt;

}
