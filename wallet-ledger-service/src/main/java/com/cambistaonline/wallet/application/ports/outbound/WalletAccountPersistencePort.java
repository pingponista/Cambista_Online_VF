package com.cambistaonline.wallet.application.ports.outbound;

import com.cambistaonline.wallet.domain.model.AccountCurrency;
import com.cambistaonline.wallet.domain.model.WalletAccount;

import java.util.List;
import java.util.Optional;

public interface WalletAccountPersistencePort {
    WalletAccount save(WalletAccount account);
    Optional<WalletAccount> findByUserEmailAndCurrency(String userEmail, AccountCurrency currency);
    List<WalletAccount> findAllByUserEmail(String userEmail);
    boolean existsByUserEmailAndCurrency(String userEmail, AccountCurrency currency);
}
