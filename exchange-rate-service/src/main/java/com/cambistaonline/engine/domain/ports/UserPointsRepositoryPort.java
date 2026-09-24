package com.cambistaonline.engine.domain.ports;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserPointsRepositoryPort {
    int getUserPointsBalance(String userEmail);

    record PointsConfig(int minRedeemable, int pointsPerBlock, BigDecimal rateImprovementPerBlock) {}

    Optional<PointsConfig> findPointsConfig(String userRole);

    void updateUserPoints(String userEmail, int pointsDeducted, int pointsEarned);
}
