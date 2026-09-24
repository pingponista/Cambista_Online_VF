package com.cambistaonline.auth.infrastructure.persistence;

import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.model.AuthProvider;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.model.UserStatus;
import com.cambistaonline.auth.domain.ports.UserRepositoryPort;
import com.cambistaonline.auth.domain.valueobjects.Dni;
import com.cambistaonline.auth.domain.valueobjects.Email;
import com.cambistaonline.auth.domain.valueobjects.Password;
import com.cambistaonline.auth.domain.valueobjects.Ruc;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort, UserPersistencePort {

    private final SpringDataUserRepository repository;

    public UserRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toEntity(user);
        UserJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.getValue()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.getValue());
    }

    private UserJpaEntity toEntity(User domain) {
        LocalDateTime createdAt = domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now();
        LocalDateTime updatedAt = domain.getUpdatedAt() != null ? domain.getUpdatedAt() : LocalDateTime.now();

        return new UserJpaEntity(
                domain.getId() != null ? domain.getId() : UUID.randomUUID(),
                domain.getEmail() != null ? domain.getEmail().getValue() : null,
                domain.getPassword() != null ? domain.getPassword().getValue() : null,
                domain.getFirstName(),
                domain.getLastName(),
                domain.getDni() != null ? domain.getDni().getValue() : null,
                domain.getCompanyName(),
                domain.getRuc() != null ? domain.getRuc().getValue() : null,
                domain.getLegalRepresentativeName(),
                domain.getRole() != null ? domain.getRole() : "N",
                domain.getStatus() != null ? domain.getStatus().name() : UserStatus.ACTIVE.name(),
                domain.isMfaEnabled(),
                domain.getMfaSecret(),
                domain.getAuthProvider() != null ? domain.getAuthProvider().name() : "LOCAL",
                domain.getProviderId(),
                createdAt,
                updatedAt
        );
    }

    private User toDomain(UserJpaEntity entity) {
        AuthProvider provider = AuthProvider.LOCAL;
        if (entity.getAuthProvider() != null) {
            try {
                provider = AuthProvider.valueOf(entity.getAuthProvider());
            } catch (IllegalArgumentException ignored) {}
        }

        return User.builder()
                .id(entity.getId())
                .email(new Email(entity.getEmail()))
                .password(Password.fromHash(entity.getPassword()))
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .dni(entity.getDni() != null ? new Dni(entity.getDni()) : null)
                .companyName(entity.getCompanyName())
                .ruc(entity.getRuc() != null ? new Ruc(entity.getRuc()) : null)
                .legalRepresentativeName(entity.getLegalRepresentativeName())
                .role(entity.getRole())
                .status(UserStatus.valueOf(entity.getStatus()))
                .mfaEnabled(entity.isMfaEnabled())
                .mfaSecret(entity.getMfaSecret())
                .authProvider(provider)
                .providerId(entity.getProviderId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
