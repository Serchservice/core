package com.serch.server.generators.chat;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * The ChatRoomID class generates unique identifiers for chat rooms.
 * It implements the IdentifierGenerator interface provided by Hibernate.
 * <p></p>
 * @see IdentifierGenerator
 */
public class ChatRoomID implements IdentifierGenerator {

    /**
     * Generates a unique identifier for chat rooms.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    @Override
    public Serializable generate(
            SharedSessionContractImplementor sharedSessionContractImplementor, Object o
    ) throws HibernateException {
        return "SCRM-" + UUID.randomUUID();
    }
}