package com.cambistaonline.wallet.infrastructure.persistence;

import com.cambistaonline.wallet.application.ports.outbound.LoyaltyPointsPersistencePort;
import com.cambistaonline.wallet.domain.model.LoyaltyPoints;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoyaltyPointsPersistenceAdapter implements LoyaltyPointsPersistencePort {

    private final SpringDataJpaLoyaltyPointsRepository repository;

    public LoyaltyPointsPersistenceAdapter(SpringDataJpaLoyaltyPointsRepository repository) {
        this.repository = repository;
    }

    @Override
    public LoyaltyPoints save(LoyaltyPoints points) {
        LoyaltyPointsJpaEntity entity = toEntity(points);
        LoyaltyPointsJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<LoyaltyPoints> findByUserEmail(String userEmail) {
        return repository.findByUserEmail(userEmail).map(this::toDomain);
    }

    private LoyaltyPointsJpaEntity toEntity(LoyaltyPoints domain) {
        return new LoyaltyPointsJpaEntity(
                domain.getId(),
                domain.getUserEmail(),
                domain.getSaldoPuntos(),
                domain.getPuntosAcumulados(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    private LoyaltyPoints toDomain(LoyaltyPointsJpaEntity entity) {
        return new LoyaltyPoints(
                entity.getId(),
                entity.getUserEmail(),
                entity.getSaldoPuntos(),
                entity.getPuntosAcumulados(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
