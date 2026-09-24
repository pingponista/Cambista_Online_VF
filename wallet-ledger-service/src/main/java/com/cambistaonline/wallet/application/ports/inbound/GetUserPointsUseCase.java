package com.cambistaonline.wallet.application.ports.inbound;

import com.cambistaonline.wallet.application.dto.UserPointsDto;

public interface GetUserPointsUseCase {
    UserPointsDto getUserPoints(String userEmail);
}
