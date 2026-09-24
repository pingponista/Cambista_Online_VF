package com.cambistaonline.wallet.application;

import com.cambistaonline.common.grpc.wallet.LockFundsCommand;
import com.cambistaonline.common.grpc.wallet.LockFundsResult;
import com.cambistaonline.common.grpc.wallet.UnlockFundsCommand;
import com.cambistaonline.common.grpc.wallet.UnlockFundsResult;
import com.cambistaonline.wallet.application.ports.inbound.AdjustUserPointsUseCase;
import com.cambistaonline.wallet.application.ports.outbound.LedgerEntryPersistencePort;
import com.cambistaonline.wallet.application.ports.outbound.WalletAccountPersistencePort;
import com.cambistaonline.wallet.application.service.WalletSagaCoordinatorService;
import com.cambistaonline.wallet.domain.model.AccountCurrency;
import com.cambistaonline.wallet.domain.model.WalletAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletSagaCoordinatorServiceTest {

    private WalletAccountPersistencePort walletPort;
    private LedgerEntryPersistencePort ledgerPort;
    private AdjustUserPointsUseCase pointsService;
    private WalletSagaCoordinatorService sagaService;

    @BeforeEach
    void setUp() {
        walletPort = Mockito.mock(WalletAccountPersistencePort.class);
        ledgerPort = Mockito.mock(LedgerEntryPersistencePort.class);
        pointsService = Mockito.mock(AdjustUserPointsUseCase.class);

        sagaService = new WalletSagaCoordinatorService(walletPort, ledgerPort, pointsService);
    }

    @Test
    @DisplayName("SAGA Bloqueo: Retiene saldo en cuenta origen y crea entrada de LOCK en Ledger")
    void shouldLockFundsSuccessfully() {
        WalletAccount account = WalletAccount.createInitial("demo@cambistaonline.pe", AccountCurrency.USD);
        account.credit(BigDecimal.valueOf(1000.00));

        when(walletPort.findByUserEmailAndCurrency("demo@cambistaonline.pe", AccountCurrency.USD))
                .thenReturn(Optional.of(account));
        when(walletPort.save(any())).thenAnswer(i -> i.getArgument(0));

        LockFundsCommand command = new LockFundsCommand(
                "TRX-001",
                "demo@cambistaonline.pe",
                "USD",
                BigDecimal.valueOf(300.00),
                "Bloqueo preventivo"
        );

        LockFundsResult result = sagaService.lockFunds(command);

        assertTrue(result.success());
        assertEquals(0, BigDecimal.valueOf(700.00).compareTo(result.remainingAvailable()));
        assertEquals(0, BigDecimal.valueOf(300.00).compareTo(account.getLockedBalance()));
        verify(walletPort, times(1)).save(account);
        verify(ledgerPort, times(1)).save(any());
    }

    @Test
    @DisplayName("SAGA Compensación: Desbloquea saldo retenido y lo devuelve a disponible")
    void shouldCompensateAndUnlockFunds() {
        WalletAccount account = WalletAccount.createInitial("demo@cambistaonline.pe", AccountCurrency.USD);
        account.credit(BigDecimal.valueOf(1000.00));
        account.lockFunds(BigDecimal.valueOf(300.00)); // Quedan 700 disponible, 300 bloqueado

        when(walletPort.findByUserEmailAndCurrency("demo@cambistaonline.pe", AccountCurrency.USD))
                .thenReturn(Optional.of(account));
        when(walletPort.save(any())).thenAnswer(i -> i.getArgument(0));

        UnlockFundsCommand command = new UnlockFundsCommand(
                "TRX-001",
                "demo@cambistaonline.pe",
                "USD",
                BigDecimal.valueOf(300.00),
                "Compensación: Tasa de cambio expirada"
        );

        UnlockFundsResult result = sagaService.unlockFunds(command);

        assertTrue(result.success());
        assertEquals(0, BigDecimal.valueOf(1000.00).compareTo(result.restoredAvailable()));
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getLockedBalance()));
        verify(walletPort, times(1)).save(account);
        verify(ledgerPort, times(1)).save(any());
    }
}
