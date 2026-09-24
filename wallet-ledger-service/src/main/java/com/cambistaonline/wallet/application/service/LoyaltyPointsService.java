package com.cambistaonline.wallet.application.service;

import com.cambistaonline.wallet.application.dto.UserPointsDto;
import com.cambistaonline.wallet.application.ports.inbound.AdjustUserPointsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.GetUserPointsUseCase;
import com.cambistaonline.wallet.application.ports.outbound.LoyaltyPointsPersistencePort;
import com.cambistaonline.wallet.domain.model.LoyaltyPoints;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoyaltyPointsService implements GetUserPointsUseCase, AdjustUserPointsUseCase {

    private final LoyaltyPointsPersistencePort pointsPort;

    public LoyaltyPointsService(LoyaltyPointsPersistencePort pointsPort) {
        this.pointsPort = pointsPort;
    }

    @Override
    @Transactional(readOnly = true)
    public UserPointsDto getUserPoints(String userEmail) {
        LoyaltyPoints points = pointsPort.findByUserEmail(userEmail)
                .orElseGet(() -> LoyaltyPoints.createInitial(userEmail, 0));
        return new UserPointsDto(points.getUserEmail(), points.getSaldoPuntos(), points.getPuntosAcumulados());
    }

    @Override
    @Transactional
    public UserPointsDto awardPoints(String userEmail, int points) {
        LoyaltyPoints loyaltyPoints = pointsPort.findByUserEmail(userEmail)
                .orElseGet(() -> LoyaltyPoints.createInitial(userEmail, 0));
        loyaltyPoints.awardPoints(points);
        LoyaltyPoints saved = pointsPort.save(loyaltyPoints);
        return new UserPointsDto(saved.getUserEmail(), saved.getSaldoPuntos(), saved.getPuntosAcumulados());
    }

    @Override
    @Transactional
    public UserPointsDto redeemPoints(String userEmail, int points) {
        LoyaltyPoints loyaltyPoints = pointsPort.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no cuenta con registro de puntos: " + userEmail));
        loyaltyPoints.redeemPoints(points);
        LoyaltyPoints saved = pointsPort.save(loyaltyPoints);
        return new UserPointsDto(saved.getUserEmail(), saved.getSaldoPuntos(), saved.getPuntosAcumulados());
    }
}
