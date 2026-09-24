package com.cambistaonline.wallet.application.service;

import com.cambistaonline.wallet.application.dto.LedgerEntryDto;
import com.cambistaonline.wallet.application.dto.LedgerMovementCommand;
import com.cambistaonline.wallet.application.dto.WalletBalanceDto;
import com.cambistaonline.wallet.application.ports.inbound.GetWalletBalancesUseCase;
import com.cambistaonline.wallet.application.ports.inbound.ProcessLedgerMovementUseCase;
import com.cambistaonline.wallet.application.ports.inbound.ProvisionUserWalletsUseCase;
import com.cambistaonline.wallet.application.ports.outbound.LedgerEntryPersistencePort;
import com.cambistaonline.wallet.application.ports.outbound.WalletAccountPersistencePort;
import com.cambistaonline.wallet.domain.exceptions.WalletNotFoundException;
import com.cambistaonline.wallet.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletLedgerService implements GetWalletBalancesUseCase, ProcessLedgerMovementUseCase, ProvisionUserWalletsUseCase {

    private final WalletAccountPersistencePort walletPort;
    private final LedgerEntryPersistencePort ledgerPort;

    public WalletLedgerService(WalletAccountPersistencePort walletPort,
                               LedgerEntryPersistencePort ledgerPort) {
        this.walletPort = walletPort;
        this.ledgerPort = ledgerPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletBalanceDto> getBalancesByUser(String userEmail) {
        return walletPort.findAllByUserEmail(userEmail).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LedgerEntryDto processMovement(LedgerMovementCommand command) {
        WalletAccount account = walletPort.findByUserEmailAndCurrency(command.userEmail(), command.currency())
                .orElseThrow(() -> new WalletNotFoundException(
                        String.format("No existe billetera en %s para el usuario %s", command.currency(), command.userEmail())));

        if (command.direction() == EntryDirection.CREDIT) {
            account.credit(command.amount());
        } else if (command.direction() == EntryDirection.DEBIT) {
            account.debit(command.amount());
        }

        walletPort.save(account);

        LedgerEntry entry = LedgerEntry.create(
                command.userEmail(),
                command.currency(),
                command.direction(),
                command.amount(),
                command.movementType(),
                command.referenceId(),
                command.description()
        );

        LedgerEntry savedEntry = ledgerPort.save(entry);

        return new LedgerEntryDto(
                savedEntry.getId(),
                savedEntry.getUserEmail(),
                savedEntry.getCurrency(),
                savedEntry.getDirection(),
                savedEntry.getAmount(),
                savedEntry.getMovementType(),
                savedEntry.getReferenceId(),
                savedEntry.getDescription(),
                savedEntry.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void provisionInitialWallets(String userEmail) {
        for (AccountCurrency currency : AccountCurrency.values()) {
            if (!walletPort.existsByUserEmailAndCurrency(userEmail, currency)) {
                WalletAccount initialAccount = WalletAccount.createInitial(userEmail, currency);
                walletPort.save(initialAccount);
            }
        }
    }

    private WalletBalanceDto toDto(WalletAccount account) {
        return new WalletBalanceDto(
                account.getId(),
                account.getUserEmail(),
                account.getCurrency(),
                account.getAvailableBalance(),
                account.getLockedBalance(),
                account.getTotalBalance(),
                account.getStatus()
        );
    }
}
