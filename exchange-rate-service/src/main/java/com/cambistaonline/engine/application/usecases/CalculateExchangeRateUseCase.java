package com.cambistaonline.engine.application.usecases;

import com.cambistaonline.engine.application.dto.CalculateRateRequest;
import com.cambistaonline.engine.application.dto.CalculateRateResponse;
import com.cambistaonline.engine.domain.model.*;
import com.cambistaonline.engine.domain.strategy.ExchangeRateCalculationPipeline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CalculateExchangeRateUseCase {
    private final ExchangeRateCalculationPipeline pipeline;

    public CalculateExchangeRateUseCase(ExchangeRateCalculationPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public CalculateRateResponse execute(CalculateRateRequest request,
                                         String userEmail,
                                         String userName,
                                         String userRole,
                                         CustomerLevel customerLevel) {
        // Security Role Validation: Only roles "J" (Juridica) or "N" (Natural) allowed
        if (userRole == null || (!userRole.equalsIgnoreCase("J") && !userRole.equalsIgnoreCase("N"))) {
            throw new IllegalArgumentException("ROLE_FORBIDDEN: User role must be 'J' or 'N'");
        }

        String normalizedRole = userRole.toUpperCase();
        CustomerLevel level = customerLevel != null ? customerLevel : CustomerLevel.PREFERENTE;

        CalculationContext context = new CalculationContext(
                userEmail,
                userName != null ? userName : userEmail,
                normalizedRole,
                level,
                request.getCurrencyOrigin(),
                request.getCurrencyDestination(),
                request.getOperationType(),
                request.getPointsToRedeem(),
                LocalDateTime.now()
        );

        // Execute dynamic calculation pipeline (Strategy Pattern)
        pipeline.execute(context);

        // Map domain context to Application DTO
        CalculateRateResponse.UserProfileData userDto = new CalculateRateResponse.UserProfileData(
                context.getUserName(),
                context.getUserEmail(),
                context.getCustomerLevel().name(),
                context.getUserRole()
        );

        CalculateRateResponse.TipoCambioData rateDto = new CalculateRateResponse.TipoCambioData(
                context.getBaseSbsRate(),
                context.getSpread(),
                context.getHourlyAdjustment(),
                context.getSeasonalAdjustment(),
                context.getPointsBenefit(),
                context.getFinalRate()
        );

        CalculateRateResponse.BeneficiosData benefitsDto = new CalculateRateResponse.BeneficiosData(
                context.getAvailablePointsBalance(),
                context.getActualPointsRedeemed()
        );

        List<CalculateRateResponse.DetalleItemData> detailDtos = context.getDetails().stream()
                .map(d -> new CalculateRateResponse.DetalleItemData(d.getConcepto(), d.getValor()))
                .collect(Collectors.toList());

        return new CalculateRateResponse(
                userDto,
                rateDto,
                benefitsDto,
                detailDtos,
                "00",
                "Tipo de cambio calculado correctamente"
        );
    }
}
