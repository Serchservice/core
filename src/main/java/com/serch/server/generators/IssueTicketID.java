package com.serch.server.generators;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The IssueTicketID class generates unique identifiers for issue tickets.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class IssueTicketID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for issue tickets.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    @Override
    @SneakyThrows
    public Serializable generate(
            SharedSessionContractImplementor sharedSessionContractImplementor, Object o
    ) {
        return "SISS-" + UUID.randomUUID();
    }
}