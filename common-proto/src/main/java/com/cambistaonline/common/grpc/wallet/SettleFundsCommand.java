package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

public record SettleFundsCommand(
        String transactionId,
        String userEmail,
        String originCurrency,
        BigDecimal originAmount,
        String destinationCurrency,
        BigDecimal destinationAmount,
        int pointsRedeemed,
        int pointsRewarded
) {}
