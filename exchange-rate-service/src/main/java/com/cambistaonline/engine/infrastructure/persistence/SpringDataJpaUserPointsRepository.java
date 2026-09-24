package com.cambistaonline.engine.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataJpaUserPointsRepository extends JpaRepository<UserPointsJpaEntity, Long> {
    Optional<UserPointsJpaEntity> findByUserEmail(String userEmail);
}
