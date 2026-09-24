package com.cambistaonline.engine.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Optional;

public interface SpringDataJpaHourlyRuleRepository extends JpaRepository<HourlyRuleJpaEntity, Long> {
    @Query("SELECT h FROM HourlyRuleJpaEntity h WHERE h.tipoUsuario = :role AND h.active = true AND :time BETWEEN h.horaInicio AND h.horaFin")
    Optional<HourlyRuleJpaEntity> findMatchingHourlyRule(@Param("role") String role, @Param("time") LocalTime time);
}
