package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

public record UnlockFundsCommand(
        String transactionId,
        String userEmail,
        String currency,
        BigDecimal amount,
        String compensationReason
) {}
