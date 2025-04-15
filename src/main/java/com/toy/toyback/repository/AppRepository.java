package com.toy.toyback.repository;

import com.toy.toyback.entity.AppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppRepository extends JpaRepository<AppEntity, Long> {

    @Query("SELECT u FROM AppEntity u WHERE u.clientId = :clientId")
    Optional<AppEntity> findByClientId(@Param("clientId") String clientId);
}
