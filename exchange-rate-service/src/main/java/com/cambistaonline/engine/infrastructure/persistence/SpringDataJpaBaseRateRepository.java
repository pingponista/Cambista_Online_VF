package com.cambistaonline.engine.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataJpaBaseRateRepository extends JpaRepository<BaseRateJpaEntity, Long> {
    @Query("SELECT r FROM BaseRateJpaEntity r WHERE r.monedaOrigen = :origin AND r.monedaDestino = :destination AND r.active = true ORDER BY r.fechaEfectiva DESC")
    Optional<BaseRateJpaEntity> findFirstActiveRate(@Param("origin") String origin, @Param("destination") String destination);
}
