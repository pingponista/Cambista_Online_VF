package com.cambistaonline.engine.infrastructure.persistence;

import com.cambistaonline.engine.domain.model.CurrencyType;
import com.cambistaonline.engine.domain.model.CustomerLevel;
import com.cambistaonline.engine.domain.model.OperationType;
import com.cambistaonline.engine.domain.ports.ExchangeRateRepositoryPort;
import com.cambistaonline.engine.domain.ports.ExchangeRuleRepositoryPort;
import com.cambistaonline.engine.domain.ports.UserPointsRepositoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Component
public class ExchangeEnginePersistenceAdapter implements ExchangeRateRepositoryPort, ExchangeRuleRepositoryPort, UserPointsRepositoryPort {

    private final SpringDataJpaBaseRateRepository baseRateRepository;
    private final SpringDataJpaCustomerSpreadRepository customerSpreadRepository;
    private final SpringDataJpaHourlyRuleRepository hourlyRuleRepository;
    private final SpringDataJpaSeasonalRuleRepository seasonalRuleRepository;
    private final SpringDataJpaPointsConfigRepository pointsConfigRepository;
    private final SpringDataJpaUserPointsRepository userPointsRepository;

    public ExchangeEnginePersistenceAdapter(SpringDataJpaBaseRateRepository baseRateRepository,
                                           SpringDataJpaCustomerSpreadRepository customerSpreadRepository,
                                           SpringDataJpaHourlyRuleRepository hourlyRuleRepository,
                                           SpringDataJpaSeasonalRuleRepository seasonalRuleRepository,
                                           SpringDataJpaPointsConfigRepository pointsConfigRepository,
                                           SpringDataJpaUserPointsRepository userPointsRepository) {
        this.baseRateRepository = baseRateRepository;
        this.customerSpreadRepository = customerSpreadRepository;
        this.hourlyRuleRepository = hourlyRuleRepository;
        this.seasonalRuleRepository = seasonalRuleRepository;
        this.pointsConfigRepository = pointsConfigRepository;
        this.userPointsRepository = userPointsRepository;
    }

    @Override
    public Optional<BigDecimal> findActiveBaseRate(CurrencyType origin, CurrencyType destination, OperationType operation) {
        return baseRateRepository.findFirstActiveRate(origin.name(), destination.name())
                .map(entity -> operation == OperationType.COMPRA ? entity.getValorCompra() : entity.getValorVenta());
    }

    @Override
    public Optional<BigDecimal> findSpread(String userRole, CustomerLevel level, OperationType operation) {
        String levelStr = level != null ? level.name() : "PREFERENTE";
        return customerSpreadRepository.findByTipoUsuarioAndNivelClienteAndActiveTrue(userRole, levelStr)
                .map(entity -> operation == OperationType.COMPRA ? entity.getSpreadCompra() : entity.getSpreadVenta());
    }

    @Override
    public Optional<BigDecimal> findHourlyAdjustment(String userRole, LocalTime time, OperationType operation) {
        return hourlyRuleRepository.findMatchingHourlyRule(userRole, time)
                .map(entity -> operation == OperationType.COMPRA ? entity.getAjusteCompra() : entity.getAjusteVenta());
    }

    @Override
    public Optional<BigDecimal> findSeasonalAdjustment(String userRole, LocalDateTime dateTime, OperationType operation) {
        return seasonalRuleRepository.findMatchingSeasonalRule(userRole, dateTime)
                .map(entity -> operation == OperationType.COMPRA ? entity.getAjusteCompra() : entity.getAjusteVenta());
    }

    @Override
    public int getUserPointsBalance(String userEmail) {
        return userPointsRepository.findByUserEmail(userEmail)
                .map(UserPointsJpaEntity::getSaldoPuntos)
                .orElse(320); // Default balance for demo if user not found
    }

    @Override
    public Optional<PointsConfig> findPointsConfig(String userRole) {
        return pointsConfigRepository.findByTipoUsuarioAndActiveTrue(userRole)
                .map(entity -> new PointsConfig(
                        entity.getCanjeMinimo(),
                        entity.getPuntosPorBloque(),
                        entity.getMejoraPorBloque()
                ));
    }

    @Override
    public void updateUserPoints(String userEmail, int pointsDeducted, int pointsEarned) {
        UserPointsJpaEntity entity = userPointsRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    UserPointsJpaEntity newEntity = new UserPointsJpaEntity();
                    newEntity.setUserEmail(userEmail);
                    newEntity.setSaldoPuntos(320);
                    newEntity.setPuntosAcumulados(1500);
                    return newEntity;
                });

        int newSaldo = Math.max(0, entity.getSaldoPuntos() - pointsDeducted + pointsEarned);
        int newAcumulados = entity.getPuntosAcumulados() + pointsEarned;

        entity.setSaldoPuntos(newSaldo);
        entity.setPuntosAcumulados(newAcumulados);

        userPointsRepository.save(entity);
    }
}
