package com.cambistaonline.wallet.application.ports.inbound;

import com.cambistaonline.common.grpc.wallet.SettleFundsCommand;
import com.cambistaonline.common.grpc.wallet.SettleFundsResult;

public interface SettleFundsUseCase {
    SettleFundsResult settleFunds(SettleFundsCommand command);
}
