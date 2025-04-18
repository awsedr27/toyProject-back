package com.toy.toyback.entity;

import jakarta.persistence.Entity;
        import jakarta.persistence.Id;
        import jakarta.persistence.Table;
        import lombok.Getter;
        import lombok.Setter;

        import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "authorization_entity")
public class AuthorizationEntity {
    @Id
    private String code;

    private String clientId;
    private String redirectUri;

    private boolean used;
    private LocalDateTime createdAt;

}