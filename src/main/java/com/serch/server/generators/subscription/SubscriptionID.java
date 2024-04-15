package com.serch.server.generators.subscription;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The SubscriptionID class generates unique identifiers for subscriptions.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class SubscriptionID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for subscriptions.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SSUB-" + UUID.randomUUID().toString().toUpperCase();
    }
}