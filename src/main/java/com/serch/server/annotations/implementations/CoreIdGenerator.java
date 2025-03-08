package com.serch.server.annotations.implementations;

import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.TransactionID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.id.IdentifierGenerator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The TransactionID class generates unique identifiers for transactions.
 */
public class CoreIdGenerator implements IdentifierGenerator {
    /**
     * Generates a unique identifier for transactions.
     *
     * @param session The session implementor
     * @param object The object
     * @return A generated unique identifier
     */
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        AtomicReference<String> prefix = new AtomicReference<>("");
        AtomicReference<String> uuid = new AtomicReference<>(UUID.randomUUID().toString());

        Optional<Field> field = Optional.of(object.getClass())
                .map(Class::getDeclaredFields)
                .flatMap(fields -> Arrays.stream(fields)
                        .filter(f -> f.isAnnotationPresent(CoreID.class) || f.isAnnotationPresent(TransactionID.class))
                        .findFirst());

        field.ifPresent(f -> {
            if (f.isAnnotationPresent(CoreID.class)) {
                CoreID core = f.getAnnotation(CoreID.class);
                prefix.set(core.prefix());

                if (core.replaceSymbols()) {
                    uuid.set(uuid.get().replaceAll("-", ""));
                }

                if (core.toUpperCase()) {
                    uuid.set(uuid.get().toUpperCase());
                }

                if (core.start() >= 0 && core.end() <= uuid.get().length() && core.start() <= core.end()) {
                    uuid.set(uuid.get().substring(core.start(), core.end()));
                }
            } else if (f.isAnnotationPresent(TransactionID.class)) {
                TransactionID transaction = f.getAnnotation(TransactionID.class);
                prefix.set(transaction.prefix());
            }
        });

        return prefix.get() + uuid.get();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}