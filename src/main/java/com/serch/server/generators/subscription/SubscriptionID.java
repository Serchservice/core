package com.serch.server.generators.subscription;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class SubscriptionID implements IdentifierGenerator {
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SSUB-"+ UUID.randomUUID().toString().toUpperCase();
    }
}