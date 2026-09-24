package com.cambistaonline.wallet.application.ports.inbound;

import com.cambistaonline.common.grpc.wallet.LockFundsCommand;
import com.cambistaonline.common.grpc.wallet.LockFundsResult;

public interface LockFundsUseCase {
    LockFundsResult lockFunds(LockFundsCommand command);
}
