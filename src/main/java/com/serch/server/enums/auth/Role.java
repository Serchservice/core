package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Role enum represents different roles in the application.
 * Each enum constant corresponds to a specific role
 * and provides a set of associated permissions and a descriptive type.
 * <p></p>
 * The roles are:
 * <ul>
 *     <li>{@link Role#USER} - Role for regular users</li>
 *     <li>{@link Role#PROVIDER} - Role for service providers</li>
 *     <li>{@link Role#ASSOCIATE_PROVIDER} - Role for associate providers</li>
 *     <li>{@link Role#BUSINESS} - Role for business entities</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 * <p></p>
 * @see Permission
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Set.of(
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.USER_UPDATE,
            Permission.USER_DELETE
    ), "User"),
    PROVIDER(Set.of(
            Permission.PROVIDER_READ,
            Permission.PROVIDER_WRITE,
            Permission.PROVIDER_UPDATE,
            Permission.PROVIDER_DELETE
    ), "Provider"),
    ASSOCIATE_PROVIDER(Set.of(
            Permission.ASSOCIATE_PROVIDER_READ,
            Permission.ASSOCIATE_PROVIDER_WRITE,
            Permission.ASSOCIATE_PROVIDER_UPDATE,
            Permission.ASSOCIATE_PROVIDER_DELETE
    ), "Associate"),
    BUSINESS(Set.of(
            Permission.BUSINESS_READ,
            Permission.BUSINESS_WRITE,
            Permission.BUSINESS_UPDATE,
            Permission.BUSINESS_DELETE
    ), "Business");

    private final Set<Permission> permissions;
    private final String type;

    /**
     * Retrieves the authorities (permissions) associated with the role.
     *
     * @return A list of SimpleGrantedAuthority objects representing the authorities.
     */
    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<>(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getType()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
