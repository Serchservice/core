package com.serch.server.generators.account;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The ReferralID class generates unique identifiers for referrals.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class ReferralID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for referrals.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SREF-" + UUID.randomUUID();
    }
}