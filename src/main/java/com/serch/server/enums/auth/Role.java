package com.serch.server.enums.auth;

import com.serch.server.admin.enums.PermissionScope;
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
 * @see AuthPermission
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    USER(
            Set.of(AuthPermission.USER_READ, AuthPermission.USER_WRITE, AuthPermission.USER_UPDATE, AuthPermission.USER_DELETE),
            Set.of(PermissionScope.USER, PermissionScope.PAYMENT, PermissionScope.SUPPORT, PermissionScope.PRODUCT),
            "User"
    ),
    PROVIDER(
            Set.of(AuthPermission.PROVIDER_READ, AuthPermission.PROVIDER_WRITE, AuthPermission.PROVIDER_UPDATE, AuthPermission.PROVIDER_DELETE),
            Set.of(PermissionScope.PROVIDER, PermissionScope.PAYMENT, PermissionScope.SUPPORT, PermissionScope.PRODUCT),
            "Provider"
    ),
    ASSOCIATE_PROVIDER(
            Set.of(AuthPermission.ASSOCIATE_PROVIDER_READ, AuthPermission.ASSOCIATE_PROVIDER_WRITE, AuthPermission.ASSOCIATE_PROVIDER_UPDATE, AuthPermission.ASSOCIATE_PROVIDER_DELETE),
            Set.of(PermissionScope.ASSOCIATE_PROVIDER, PermissionScope.PAYMENT, PermissionScope.SUPPORT, PermissionScope.PRODUCT),
            "Associate"
    ),
    BUSINESS(
            Set.of(AuthPermission.BUSINESS_READ, AuthPermission.BUSINESS_WRITE, AuthPermission.BUSINESS_UPDATE, AuthPermission.BUSINESS_DELETE),
            Set.of(PermissionScope.BUSINESS, PermissionScope.PAYMENT, PermissionScope.SUPPORT, PermissionScope.PRODUCT),
            "Business"
    ),
    SUPER_ADMIN(Set.of(), Set.of(), "Super Administrator"),
    ADMIN(Set.of(), Set.of(PermissionScope.ADMIN), "Administrator"),
    MANAGER(Set.of(), Set.of(PermissionScope.ADMIN),"Manager"),
    TEAM(Set.of(), Set.of(PermissionScope.ADMIN),"Team");

    private final Set<AuthPermission> authPermissions;
    private final Set<PermissionScope> scopes;
    private final String type;

    /**
     * Retrieves the authorities (permissions) associated with the role.
     *
     * @return A list of SimpleGrantedAuthority objects representing the authorities.
     */
    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<>(getAuthPermissions()
                .stream()
                .map(authPermission -> new SimpleGrantedAuthority(authPermission.getType()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
