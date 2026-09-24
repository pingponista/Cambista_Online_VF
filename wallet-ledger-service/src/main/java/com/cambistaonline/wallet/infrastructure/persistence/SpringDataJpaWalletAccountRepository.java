package com.cambistaonline.wallet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaWalletAccountRepository extends JpaRepository<WalletAccountJpaEntity, UUID> {
    Optional<WalletAccountJpaEntity> findByUserEmailAndCurrency(String userEmail, String currency);
    List<WalletAccountJpaEntity> findByUserEmail(String userEmail);
    boolean existsByUserEmailAndCurrency(String userEmail, String currency);
}
