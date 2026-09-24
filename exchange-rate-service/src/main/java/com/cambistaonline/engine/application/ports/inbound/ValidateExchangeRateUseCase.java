package com.cambistaonline.engine.application.ports.inbound;

import com.cambistaonline.common.grpc.rate.ValidateRateQuery;
import com.cambistaonline.common.grpc.rate.ValidateRateResult;

public interface ValidateExchangeRateUseCase {
    ValidateRateResult validateRate(ValidateRateQuery query);
}
