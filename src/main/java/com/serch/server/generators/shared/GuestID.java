package com.serch.server.generators.shared;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class GuestID implements IdentifierGenerator {
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SGST-"+ UUID.randomUUID().toString().toUpperCase();
    }
}