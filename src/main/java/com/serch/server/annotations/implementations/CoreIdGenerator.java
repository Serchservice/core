package com.serch.server.annotations.implementations;

import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.TransactionID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;
import java.util.UUID;

/**
 * The TransactionID class generates unique identifiers for transactions.
 */
public class CoreIdGenerator implements BeforeExecutionGenerator {
    /**
     * Generates a unique identifier for transactions.
     *
     * @param sharedSessionContractImplementor The session implementor
     * @param o                                 The object
     * @return A generated unique identifier
     */
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o, Object o1, EventType eventType) {
        String prefix = "";
        String uuid = UUID.randomUUID().toString();

        if(o.getClass().getAnnotation(CoreID.class) != null) {
            CoreID core = o.getClass().getAnnotation(CoreID.class);
            prefix = core.prefix();

            if (core.replaceSymbols()) {
                uuid = uuid.replaceAll("-", "");
            }

            if (core.toUpperCase()) {
                uuid = uuid.toUpperCase();
            }

            if (core.start() >= 0 && core.end() <= uuid.length() && core.start() <= core.end()) {
                uuid = uuid.substring(core.start(), core.end());
            }
        } else if(o.getClass().getAnnotation(TransactionID.class) != null) {
            TransactionID transaction = o.getClass().getAnnotation(TransactionID.class);
            prefix = transaction.prefix();
        }

        return prefix + uuid;
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}