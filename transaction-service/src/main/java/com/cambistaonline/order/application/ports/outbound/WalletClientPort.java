package com.cambistaonline.order.application.ports.outbound;

import com.cambistaonline.common.grpc.wallet.*;

public interface WalletClientPort {
    LockFundsResult lockFunds(LockFundsCommand command);
    UnlockFundsResult unlockFunds(UnlockFundsCommand command);
    SettleFundsResult settleFunds(SettleFundsCommand command);
}
