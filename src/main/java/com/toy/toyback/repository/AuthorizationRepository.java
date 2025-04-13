package com.toy.toyback.repository;

import com.toy.toyback.entity.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationRepository extends JpaRepository<AuthorizationEntity, String> {
}
