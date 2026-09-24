package com.cambistaonline.engine.application.ports.inbound;

import com.cambistaonline.engine.application.dto.CalculateRateRequest;
import com.cambistaonline.engine.application.dto.CalculateRateResponse;
import com.cambistaonline.engine.domain.model.CustomerLevel;

public interface CalculateExchangeRateUseCase {
    CalculateRateResponse execute(CalculateRateRequest request,
                                  String userEmail,
                                  String userName,
                                  String userRole,
                                  CustomerLevel customerLevel);
}
