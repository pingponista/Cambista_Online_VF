package com.cambistaonline.wallet.application.ports.outbound;

import com.cambistaonline.wallet.domain.model.LoyaltyPoints;

import java.util.Optional;

public interface LoyaltyPointsPersistencePort {
    LoyaltyPoints save(LoyaltyPoints points);
    Optional<LoyaltyPoints> findByUserEmail(String userEmail);
}
