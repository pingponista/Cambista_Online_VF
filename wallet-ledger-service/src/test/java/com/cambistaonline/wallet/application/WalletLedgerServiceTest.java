package com.cambistaonline.wallet.application;

import com.cambistaonline.wallet.application.dto.LedgerEntryDto;
import com.cambistaonline.wallet.application.dto.LedgerMovementCommand;
import com.cambistaonline.wallet.application.dto.WalletBalanceDto;
import com.cambistaonline.wallet.application.ports.outbound.LedgerEntryPersistencePort;
import com.cambistaonline.wallet.application.ports.outbound.WalletAccountPersistencePort;
import com.cambistaonline.wallet.application.service.WalletLedgerService;
import com.cambistaonline.wallet.domain.exceptions.InsufficientFundsException;
import com.cambistaonline.wallet.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletLedgerServiceTest {

    private WalletAccountPersistencePort walletPort;
    private LedgerEntryPersistencePort ledgerPort;
    private WalletLedgerService service;

    @BeforeEach
    void setUp() {
        walletPort = Mockito.mock(WalletAccountPersistencePort.class);
        ledgerPort = Mockito.mock(LedgerEntryPersistencePort.class);
        service = new WalletLedgerService(walletPort, ledgerPort);
    }

    @Test
    @DisplayName("Debe aprovisionar las tres billeteras (PEN, USD, EUR) para un usuario nuevo")
    void shouldProvisionWalletsSuccessfully() {
        when(walletPort.existsByUserEmailAndCurrency(anyString(), any(AccountCurrency.class))).thenReturn(false);

        service.provisionInitialWallets("nuevo@cambistaonline.pe");

        verify(walletPort, times(3)).save(any(WalletAccount.class));
    }

    @Test
    @DisplayName("Debe procesar un crédito en el saldo y registrar entrada en el Ledger")
    void shouldProcessCreditSuccessfully() {
        WalletAccount account = WalletAccount.createInitial("demo@cambistaonline.pe", AccountCurrency.PEN);
        when(walletPort.findByUserEmailAndCurrency("demo@cambistaonline.pe", AccountCurrency.PEN))
                .thenReturn(Optional.of(account));
        when(walletPort.save(any())).thenAnswer(i -> i.getArgument(0));
        when(ledgerPort.save(any())).thenAnswer(i -> i.getArgument(0));

        LedgerMovementCommand command = new LedgerMovementCommand(
                "demo@cambistaonline.pe",
                AccountCurrency.PEN,
                EntryDirection.CREDIT,
                BigDecimal.valueOf(500.00),
                LedgerMovementType.DEPOSIT,
                "REF-001",
                "Depósito bancario"
        );

        LedgerEntryDto result = service.processMovement(command);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(500.00), account.getAvailableBalance());
        verify(ledgerPort, times(1)).save(any(LedgerEntry.class));
    }

    @Test
    @DisplayName("Debe rechazar débito si el saldo es insuficiente lanzando InsufficientFundsException")
    void shouldThrowWhenInsufficientFunds() {
        WalletAccount account = WalletAccount.createInitial("demo@cambistaonline.pe", AccountCurrency.USD);
        when(walletPort.findByUserEmailAndCurrency("demo@cambistaonline.pe", AccountCurrency.USD))
                .thenReturn(Optional.of(account));

        LedgerMovementCommand command = new LedgerMovementCommand(
                "demo@cambistaonline.pe",
                AccountCurrency.USD,
                EntryDirection.DEBIT,
                BigDecimal.valueOf(100.00),
                LedgerMovementType.EXCHANGE_DEBIT,
                "REF-002",
                "Retiro sin fondos"
        );

        assertThrows(InsufficientFundsException.class, () -> service.processMovement(command));
    }
}
