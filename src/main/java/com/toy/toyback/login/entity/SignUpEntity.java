package com.toy.toyback.login.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpEntity {

    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    private String id;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name",  length = 100)
    private String name;

}

