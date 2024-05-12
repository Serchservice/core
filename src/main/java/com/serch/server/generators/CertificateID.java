package com.serch.server.generators;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The CertificateID class generates unique identifiers for certificates.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class CertificateID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for certificates.
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
        return "SCERT-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase().replaceAll("-", "");
    }
}
