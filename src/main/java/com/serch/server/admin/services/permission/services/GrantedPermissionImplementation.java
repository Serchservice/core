package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.models.GrantedPermissionScope;
import com.serch.server.admin.repositories.RequestedPermissionRepository;
import com.serch.server.admin.services.permission.responses.GrantedPermissionScopeResponse;
import com.serch.server.admin.services.permission.responses.PermissionResponse;
import com.serch.server.admin.services.permission.responses.SpecificPermissionResponse;
import com.serch.server.core.notification.repository.NotificationRepository;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrantedPermissionImplementation implements GrantedPermissionService {
    private final NotificationRepository notificationRepository;
    private final RequestedPermissionRepository requestedPermissionRepository;

    @Override
    @Transactional
    public List<GrantedPermissionScopeResponse> getGrantedClusterPermissions(Admin admin) {
        if(admin.getScopes() != null && !admin.getScopes().isEmpty()) {
            return admin.getScopes().stream().filter(scope -> scope.getAccount() == null)
                    .map(scope -> {
                        GrantedPermissionScopeResponse response = AdminMapper.instance.response(scope);
                        response.setCreatedAt(TimeUtil.formatDay(scope.getCreatedAt(), admin.getUser().getTimezone()));
                        response.setUpdatedAt(TimeUtil.formatDay(scope.getUpdatedAt(), admin.getUser().getTimezone()));
                        return getGrantedResponse(scope, response, admin);
                    }).toList();
        }

        return List.of();
    }

    private GrantedPermissionScopeResponse getGrantedResponse(GrantedPermissionScope scope, GrantedPermissionScopeResponse response, Admin admin) {
        response.setPermissions(scope.getPermissions().stream().map(permission -> {
            PermissionResponse permit = new PermissionResponse();
            permit.setPermission(permission.getPermission());
            permit.setId(permission.getId());
            permit.setCreatedAt(TimeUtil.formatDay(permission.getCreatedAt(), scope.getAdmin().getUser().getTimezone()));
            permit.setUpdatedAt(TimeUtil.formatDay(permission.getUpdatedAt(), scope.getAdmin().getUser().getTimezone()));

            requestedPermissionRepository.findGranted(admin.getId(), scope.getScope(), scope.getAccount(), permission.getPermission())
                    .ifPresent(requested -> permit.setExpiration(requested.getExpirationTime(admin.getUser().getTimezone())));
            return permit;
        }).toList());

        return response;
    }

    @Override
    @Transactional
    public List<SpecificPermissionResponse> getGrantedSpecificPermissions(Admin admin) {
        if(admin.getScopes() != null && !admin.getScopes().isEmpty()) {
            Map<String, List<GrantedPermissionScope>> groups = admin.getScopes()
                    .stream()
                    .filter(scope -> scope.getAccount() != null)
                    .collect(Collectors.groupingBy(GrantedPermissionScope::getAccount));

            List<SpecificPermissionResponse> list = new ArrayList<>();
            groups.forEach((key, value) -> {
                SpecificPermissionResponse response = new SpecificPermissionResponse();
                response.setAccount(key);
                response.setName(notificationRepository.getName(key));
                response.setScopes(value.stream().map(scope -> {
                    GrantedPermissionScopeResponse permit = AdminMapper.instance.response(scope);
                    permit.setCreatedAt(TimeUtil.formatDay(scope.getCreatedAt(), admin.getUser().getTimezone()));
                    permit.setUpdatedAt(TimeUtil.formatDay(scope.getUpdatedAt(), admin.getUser().getTimezone()));

                    return getGrantedResponse(scope, permit, admin);
                }).toList());

                list.add(response);
            });

            return list;
        }

        return List.of();
    }
}
