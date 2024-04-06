package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<>(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getType()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
