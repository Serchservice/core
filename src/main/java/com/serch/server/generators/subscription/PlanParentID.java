package com.serch.server.generators.subscription;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class PlanParentID implements IdentifierGenerator {
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SPLN-"+ UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}