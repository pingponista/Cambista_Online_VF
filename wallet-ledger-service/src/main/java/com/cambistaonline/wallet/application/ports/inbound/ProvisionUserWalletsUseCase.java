package com.cambistaonline.wallet.application.ports.inbound;

public interface ProvisionUserWalletsUseCase {
    void provisionInitialWallets(String userEmail);
}
