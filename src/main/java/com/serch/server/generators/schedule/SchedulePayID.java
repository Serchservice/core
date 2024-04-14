package com.serch.server.generators.schedule;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The SchedulePayID class generates unique identifiers for payment schedules.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class SchedulePayID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for payment schedules.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    @Override
    @SneakyThrows
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return "SSCH-PAY-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
    }
}