package com.serch.server.admin.mappers;

import com.serch.server.admin.models.transaction.ResolvedTransaction;
import com.serch.server.admin.services.scopes.payment.responses.payouts.PayoutResponse;
import com.serch.server.admin.services.scopes.payment.responses.transactions.ResolvedTransactionResponse;
import com.serch.server.admin.services.scopes.payment.responses.transactions.TransactionScopeResponse;
import com.serch.server.admin.services.scopes.payment.responses.wallet.WalletScopeResponse;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScopeMapper {
    ScopeMapper instance = Mappers.getMapper(ScopeMapper.class);

    @Mapping(target = "sender", source = "sender", ignore = true)
    TransactionScopeResponse transaction(Transaction transaction);

    @Mapping(target = "transaction", source = "transaction", ignore = true)
    ResolvedTransactionResponse resolved(ResolvedTransaction transaction);

    @Mapping(target = "lastPayoutDate", source = "lastPayday")
    WalletScopeResponse wallet(Wallet wallet);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "transaction", source = "id")
    })
    PayoutResponse payout(Transaction transaction);
}
