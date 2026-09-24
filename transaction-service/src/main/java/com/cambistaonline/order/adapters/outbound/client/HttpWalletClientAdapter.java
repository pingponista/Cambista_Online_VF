package com.cambistaonline.order.adapters.outbound.client;

import com.cambistaonline.common.grpc.wallet.*;
import com.cambistaonline.order.application.ports.outbound.WalletClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class HttpWalletClientAdapter implements WalletClientPort {

    private static final Logger log = LoggerFactory.getLogger(HttpWalletClientAdapter.class);

    private final RestTemplate restTemplate;

    @Value("${services.wallet-ledger.url:http://localhost:8083}")
    private String walletServiceUrl;

    public HttpWalletClientAdapter(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    @Override
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "walletLedgerService")
    @io.github.resilience4j.bulkhead.annotation.Bulkhead(name = "walletLedgerService")
    public LockFundsResult lockFunds(LockFundsCommand command) {
        String endpoint = walletServiceUrl + "/internal/grpc/wallet/lock";
        try {
            log.info("[WALLET-RPC] Bloqueando fondos para trx={} en {}", command.transactionId(), endpoint);
            ResponseEntity<LockFundsResult> response = restTemplate.postForEntity(endpoint, command, LockFundsResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return LockFundsResult.failure(command.transactionId(), "Respuesta inesperada de wallet-service");
        } catch (Exception e) {
            log.error("[WALLET-RPC] Error al invocar bloqueo de fondos en wallet-service: {}", e.getMessage());
            return LockFundsResult.failure(command.transactionId(), "Error de conexión con wallet-ledger-service: " + e.getMessage());
        }
    }

    @Override
    public UnlockFundsResult unlockFunds(UnlockFundsCommand command) {
        String endpoint = walletServiceUrl + "/internal/grpc/wallet/unlock";
        try {
            log.warn("[WALLET-RPC-COMPENSACIÓN] Desbloqueando fondos para trx={} en {}", command.transactionId(), endpoint);
            ResponseEntity<UnlockFundsResult> response = restTemplate.postForEntity(endpoint, command, UnlockFundsResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return UnlockFundsResult.failure(command.transactionId(), "Respuesta inesperada en compensación");
        } catch (Exception e) {
            log.error("[WALLET-RPC-COMPENSACIÓN] Error al invocar compensación en wallet-service: {}", e.getMessage());
            return UnlockFundsResult.failure(command.transactionId(), "Error en compensación: " + e.getMessage());
        }
    }

    @Override
    public SettleFundsResult settleFunds(SettleFundsCommand command) {
        String endpoint = walletServiceUrl + "/internal/grpc/wallet/settle";
        try {
            log.info("[WALLET-RPC] Liquidando fondos para trx={} en {}", command.transactionId(), endpoint);
            ResponseEntity<SettleFundsResult> response = restTemplate.postForEntity(endpoint, command, SettleFundsResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return SettleFundsResult.failure(command.transactionId(), "Respuesta inesperada en liquidación");
        } catch (Exception e) {
            log.error("[WALLET-RPC] Error al invocar liquidación en wallet-service: {}", e.getMessage());
            return SettleFundsResult.failure(command.transactionId(), "Error de liquidación: " + e.getMessage());
        }
    }
}
