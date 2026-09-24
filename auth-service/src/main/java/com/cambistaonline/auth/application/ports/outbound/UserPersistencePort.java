package com.cambistaonline.auth.application.ports.outbound;

import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;
import java.util.Optional;
import java.util.UUID;

public interface UserPersistencePort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
}
