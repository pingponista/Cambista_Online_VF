package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

public record LockFundsCommand(
        String transactionId,
        String userEmail,
        String currency,
        BigDecimal amount,
        String reason
) {}
