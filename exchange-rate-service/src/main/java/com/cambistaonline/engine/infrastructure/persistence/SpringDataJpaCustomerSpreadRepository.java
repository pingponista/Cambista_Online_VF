package com.cambistaonline.engine.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataJpaCustomerSpreadRepository extends JpaRepository<CustomerSpreadJpaEntity, Long> {
    Optional<CustomerSpreadJpaEntity> findByTipoUsuarioAndNivelClienteAndActiveTrue(String tipoUsuario, String nivelCliente);
}
