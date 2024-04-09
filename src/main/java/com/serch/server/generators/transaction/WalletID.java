package com.serch.server.generators.transaction;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class WalletID implements IdentifierGenerator {
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SWLT-"+ UUID.randomUUID()
                .toString()
                .substring(0, 10)
                .replaceAll("-", "")
                .toUpperCase();
    }
}