package com.serch.server.annotations;

import com.serch.server.annotations.implementations.CoreIdGenerator;
import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines the configuration for generating unique identifiers.
 * It allows customization of the ID generation process by specifying
 * a prefix, controlling symbol replacement, and defining a substring range.
 *
 * @author [Your Name]
 */
@IdGeneratorType(CoreIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface CoreID {
    /**
     * The name of the ID generator.
     * Defaults to "generator".
     *
     * @return The name of the ID generator.
     */
    String name() default "generator";

    /**
     * The prefix to be prepended to the generated ID.
     * Defaults to "S".
     *
     * @return The prefix for the ID.
     */
    String prefix() default "S";

    /**
     * Indicates whether to replace symbols (e.g., hyphens) in the generated UUID.
     * Defaults to false.
     *
     * @return True if symbols should be replaced, false otherwise.
     */
    boolean replaceSymbols() default false;

    /**
     * The starting index (inclusive) of the substring to be extracted from the UUID.
     * Defaults to 0.
     *
     * @return The starting index of the substring.
     */
    int start() default 0;

    /**
     * The ending index (exclusive) of the substring to be extracted from the UUID.
     * Defaults to Integer.MAX_VALUE.
     *
     * @return The ending index of the substring.
     */
    int end() default Integer.MAX_VALUE;

    /**
     * Indicates whether to convert the extracted substring to uppercase.
     * Defaults to false.
     *
     * @return True if the substring should be converted to uppercase, false otherwise.
     */
    boolean toUpperCase() default false;
}