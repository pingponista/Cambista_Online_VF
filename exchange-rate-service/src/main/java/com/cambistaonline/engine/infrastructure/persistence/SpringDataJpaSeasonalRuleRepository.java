package com.cambistaonline.engine.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataJpaSeasonalRuleRepository extends JpaRepository<SeasonalRuleJpaEntity, Long> {
    @Query("SELECT s FROM SeasonalRuleJpaEntity s WHERE s.tipoUsuario = :role AND s.active = true AND :dateTime BETWEEN s.fechaInicio AND s.fechaFin")
    Optional<SeasonalRuleJpaEntity> findMatchingSeasonalRule(@Param("role") String role, @Param("dateTime") LocalDateTime dateTime);
}
