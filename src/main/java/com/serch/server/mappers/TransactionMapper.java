package com.serch.server.mappers;

import com.serch.server.models.transaction.Wallet;
import com.serch.server.services.transaction.responses.WalletResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mappings({
            @Mapping(target = "balance", source = "balance", ignore = true),
            @Mapping(target = "deposit", source = "deposit", ignore = true)
    })
    WalletResponse wallet(Wallet wallet);
}
