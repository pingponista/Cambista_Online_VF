package com.cambistaonline.wallet.application.dto;

public record UserPointsDto(
        String userEmail,
        int saldoPuntos,
        int puntosAcumulados
) {}
