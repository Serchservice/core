package com.serch.server.generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The CallID class generates unique identifiers for calls.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class CallID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for calls.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     * @throws HibernateException If an error occurs while generating the identifier
     */
    @Override
    public Serializable generate(
            SharedSessionContractImplementor sharedSessionContractImplementor, Object o
    ) throws HibernateException {
        return "SCALL-" + UUID.randomUUID();
    }
}