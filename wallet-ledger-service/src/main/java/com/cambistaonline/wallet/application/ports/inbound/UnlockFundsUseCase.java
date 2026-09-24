package com.cambistaonline.wallet.application.ports.inbound;

import com.cambistaonline.common.grpc.wallet.UnlockFundsCommand;
import com.cambistaonline.common.grpc.wallet.UnlockFundsResult;

public interface UnlockFundsUseCase {
    UnlockFundsResult unlockFunds(UnlockFundsCommand command);
}
