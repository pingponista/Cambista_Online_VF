package com.cambistaonline.wallet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataJpaLoyaltyPointsRepository extends JpaRepository<LoyaltyPointsJpaEntity, Long> {
    Optional<LoyaltyPointsJpaEntity> findByUserEmail(String userEmail);
}
