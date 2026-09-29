package com.cambistaonline.wallet.application.service;

import com.cambistaonline.common.grpc.wallet.*;
import com.cambistaonline.wallet.application.ports.inbound.AdjustUserPointsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.LockFundsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.SettleFundsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.UnlockFundsUseCase;
import com.cambistaonline.wallet.application.ports.outbound.LedgerEntryPersistencePort;
import com.cambistaonline.wallet.application.ports.outbound.WalletAccountPersistencePort;
import com.cambistaonline.wallet.domain.exceptions.InsufficientFundsException;
import com.cambistaonline.wallet.domain.exceptions.WalletNotFoundException;
import com.cambistaonline.wallet.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletSagaCoordinatorService implements LockFundsUseCase, UnlockFundsUseCase, SettleFundsUseCase {

    private static final Logger log = LoggerFactory.getLogger(WalletSagaCoordinatorService.class);

    private final WalletAccountPersistencePort walletPort;
    private final LedgerEntryPersistencePort ledgerPort;
    private final AdjustUserPointsUseCase pointsService;

    public WalletSagaCoordinatorService(WalletAccountPersistencePort walletPort,
                                       LedgerEntryPersistencePort ledgerPort,
                                       AdjustUserPointsUseCase pointsService) {
        this.walletPort = walletPort;
        this.ledgerPort = ledgerPort;
        this.pointsService = pointsService;
    }

    @Override
    @Transactional
    public LockFundsResult lockFunds(LockFundsCommand command) {
        log.info("[SAGA WALLET] 🔒 Solicitud de bloqueo: trx={} usuario={} moneda={} monto={}",
                command.transactionId(), command.userEmail(), command.currency(), command.amount());

        AccountCurrency currency;
        try {
            currency = AccountCurrency.valueOf(command.currency().toUpperCase());
        } catch (IllegalArgumentException e) {
            return LockFundsResult.failure(command.transactionId(), "Moneda inválida: " + command.currency());
        }

        WalletAccount account = walletPort.findByUserEmailAndCurrency(command.userEmail(), currency)
                .orElseGet(() -> {
                    log.info("[SAGA WALLET] Auto-aprovisionando billeteras para usuario {}", command.userEmail());
                    for (AccountCurrency c : AccountCurrency.values()) {
                        if (!walletPort.existsByUserEmailAndCurrency(command.userEmail(), c)) {
                            walletPort.save(WalletAccount.createInitial(command.userEmail(), c));
                        }
                    }
                    return walletPort.findByUserEmailAndCurrency(command.userEmail(), currency).orElse(null);
                });

        if (account == null) {
            return LockFundsResult.failure(command.transactionId(), "No existe billetera para el usuario en " + currency);
        }

        // Si el usuario no tiene suficiente saldo disponible para la operación (ej. usuario nuevo en modo demo),
        // proveer saldo suficiente para permitir la simulación de la orden:
        if (account.getAvailableBalance().compareTo(command.amount()) < 0) {
            BigDecimal topup = command.amount().multiply(BigDecimal.valueOf(2));
            log.info("[SAGA WALLET] Saldo insuficiente en cuenta demo. Recargando preventivamente trx={} usuario={} (+{} {})",
                    command.transactionId(), command.userEmail(), topup, currency);
            account.credit(topup);
            walletPort.save(account);
        }

        try {
            account.lockFunds(command.amount());
            walletPort.save(account);

            ledgerPort.save(LedgerEntry.create(
                    command.userEmail(),
                    currency,
                    EntryDirection.DEBIT,
                    command.amount(),
                    LedgerMovementType.LOCK,
                    command.transactionId(),
                    "Bloqueo de fondos por orden " + command.transactionId() + ": " + command.reason()
            ));

            return LockFundsResult.success(command.transactionId(), account.getAvailableBalance());
        } catch (InsufficientFundsException e) {
            log.warn("[SAGA WALLET] ❌ Saldo insuficiente para bloqueo trx={}: {}", command.transactionId(), e.getMessage());
            return LockFundsResult.failure(command.transactionId(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public UnlockFundsResult unlockFunds(UnlockFundsCommand command) {
        log.info("[SAGA COMPENSACIÓN] 🔓 Desbloqueando fondos retenidos: trx={} motivo='{}'",
                command.transactionId(), command.compensationReason());

        AccountCurrency currency;
        try {
            currency = AccountCurrency.valueOf(command.currency().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UnlockFundsResult.failure(command.transactionId(), "Moneda inválida");
        }

        WalletAccount account = walletPort.findByUserEmailAndCurrency(command.userEmail(), currency)
                .orElse(null);

        if (account == null) {
            return UnlockFundsResult.failure(command.transactionId(), "Billetera no encontrada");
        }

        try {
            account.releaseFunds(command.amount());
            walletPort.save(account);

            ledgerPort.save(LedgerEntry.create(
                    command.userEmail(),
                    currency,
                    EntryDirection.CREDIT,
                    command.amount(),
                    LedgerMovementType.RELEASE,
                    command.transactionId(),
                    "COMPENSACIÓN SAGA: " + command.compensationReason()
            ));

            log.info("[SAGA COMPENSACIÓN] ✅ Fondos liberados y devueltos al disponible: {} {}", command.amount(), currency);
            return UnlockFundsResult.success(command.transactionId(), account.getAvailableBalance());
        } catch (Exception e) {
            log.error("[SAGA COMPENSACIÓN] ❌ Error liberando fondos trx={}: {}", command.transactionId(), e.getMessage());
            return UnlockFundsResult.failure(command.transactionId(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public SettleFundsResult settleFunds(SettleFundsCommand command) {
        log.info("[SAGA LIQUIDACIÓN] 💳 Liquidando fondos: trx={} usuario={}", command.transactionId(), command.userEmail());

        AccountCurrency origCurr = AccountCurrency.valueOf(command.originCurrency().toUpperCase());
        AccountCurrency destCurr = AccountCurrency.valueOf(command.destinationCurrency().toUpperCase());

        WalletAccount origAccount = walletPort.findByUserEmailAndCurrency(command.userEmail(), origCurr)
                .orElseGet(() -> walletPort.save(WalletAccount.createInitial(command.userEmail(), origCurr)));
        WalletAccount destAccount = walletPort.findByUserEmailAndCurrency(command.userEmail(), destCurr)
                .orElseGet(() -> walletPort.save(WalletAccount.createInitial(command.userEmail(), destCurr)));

        // 1. Deducir del balance bloqueado en cuenta origen
        if (origAccount.getLockedBalance().compareTo(command.originAmount()) >= 0) {
            origAccount.releaseFunds(command.originAmount());
            origAccount.debit(command.originAmount());
        } else {
            origAccount.debit(command.originAmount());
        }
        walletPort.save(origAccount);

        // 2. Acreditar a cuenta destino
        destAccount.credit(command.destinationAmount());
        walletPort.save(destAccount);

        // 3. Registrar libro mayor
        ledgerPort.save(LedgerEntry.create(command.userEmail(), origCurr, EntryDirection.DEBIT,
                command.originAmount(), LedgerMovementType.EXCHANGE_DEBIT, command.transactionId(), "Liquidación SAGA final: Débito origen"));

        ledgerPort.save(LedgerEntry.create(command.userEmail(), destCurr, EntryDirection.CREDIT,
                command.destinationAmount(), LedgerMovementType.EXCHANGE_CREDIT, command.transactionId(), "Liquidación SAGA final: Crédito destino"));

        // 4. CambiPuntos
        if (command.pointsRedeemed() > 0) {
            pointsService.redeemPoints(command.userEmail(), command.pointsRedeemed());
        }
        if (command.pointsRewarded() > 0) {
            pointsService.awardPoints(command.userEmail(), command.pointsRewarded());
        }

        return SettleFundsResult.success(command.transactionId());
    }
}
