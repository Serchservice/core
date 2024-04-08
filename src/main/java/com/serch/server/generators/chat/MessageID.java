package com.serch.server.generators.chat;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class MessageID implements IdentifierGenerator {
    @Override
    public Serializable generate(
            SharedSessionContractImplementor sharedSessionContractImplementor, Object o
    ) throws HibernateException {
        return "SMSG-"+ UUID.randomUUID();
    }
}