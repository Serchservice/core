package com.serch.server.generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The ProductID class generates unique identifiers for products.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class ProductID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for products.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    @Override
    public Serializable generate(
            SharedSessionContractImplementor sharedSessionContractImplementor, Object o
    ) throws HibernateException {
        return "SPR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}