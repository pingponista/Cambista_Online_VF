package com.cambistaonline.wallet.application.ports.inbound;

import com.cambistaonline.wallet.application.dto.UserPointsDto;

public interface AdjustUserPointsUseCase {
    UserPointsDto awardPoints(String userEmail, int points);
    UserPointsDto redeemPoints(String userEmail, int points);
}
