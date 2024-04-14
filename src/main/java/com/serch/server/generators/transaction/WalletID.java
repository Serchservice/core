package com.serch.server.generators.transaction;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The WalletID class generates unique identifiers for wallets.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class WalletID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for wallets.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SWLT-" + UUID.randomUUID()
                .toString()
                .substring(0, 10)
                .replaceAll("-", "")
                .toUpperCase();
    }
}