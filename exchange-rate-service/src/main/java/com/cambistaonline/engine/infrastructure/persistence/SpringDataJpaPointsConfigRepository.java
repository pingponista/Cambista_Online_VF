package com.cambistaonline.engine.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataJpaPointsConfigRepository extends JpaRepository<PointsConfigJpaEntity, Long> {
    Optional<PointsConfigJpaEntity> findByTipoUsuarioAndActiveTrue(String tipoUsuario);
}
