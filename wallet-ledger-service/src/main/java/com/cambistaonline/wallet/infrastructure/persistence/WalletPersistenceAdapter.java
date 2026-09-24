package com.cambistaonline.wallet.infrastructure.persistence;

import com.cambistaonline.wallet.application.ports.outbound.WalletAccountPersistencePort;
import com.cambistaonline.wallet.domain.model.AccountCurrency;
import com.cambistaonline.wallet.domain.model.AccountStatus;
import com.cambistaonline.wallet.domain.model.WalletAccount;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WalletPersistenceAdapter implements WalletAccountPersistencePort {

    private final SpringDataJpaWalletAccountRepository repository;

    public WalletPersistenceAdapter(SpringDataJpaWalletAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public WalletAccount save(WalletAccount account) {
        WalletAccountJpaEntity entity = toEntity(account);
        WalletAccountJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<WalletAccount> findByUserEmailAndCurrency(String userEmail, AccountCurrency currency) {
        return repository.findByUserEmailAndCurrency(userEmail, currency.name())
                .map(this::toDomain);
    }

    @Override
    public List<WalletAccount> findAllByUserEmail(String userEmail) {
        return repository.findByUserEmail(userEmail).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserEmailAndCurrency(String userEmail, AccountCurrency currency) {
        return repository.existsByUserEmailAndCurrency(userEmail, currency.name());
    }

    private WalletAccountJpaEntity toEntity(WalletAccount domain) {
        return new WalletAccountJpaEntity(
                domain.getId(),
                domain.getUserEmail(),
                domain.getCurrency().name(),
                domain.getAvailableBalance(),
                domain.getLockedBalance(),
                domain.getStatus().name(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    private WalletAccount toDomain(WalletAccountJpaEntity entity) {
        return new WalletAccount(
                entity.getId(),
                entity.getUserEmail(),
                AccountCurrency.valueOf(entity.getCurrency()),
                entity.getAvailableBalance(),
                entity.getLockedBalance(),
                AccountStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
