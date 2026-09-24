package com.cambistaonline.wallet.application.ports.inbound;

import com.cambistaonline.wallet.application.dto.WalletBalanceDto;

import java.util.List;

public interface GetWalletBalancesUseCase {
    List<WalletBalanceDto> getBalancesByUser(String userEmail);
}
