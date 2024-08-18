package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.enums.*;
import com.serch.server.admin.exceptions.PermissionException;
import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.*;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.repositories.GrantedPermissionRepository;
import com.serch.server.admin.repositories.GrantedPermissionScopeRepository;
import com.serch.server.admin.repositories.RequestedPermissionRepository;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.admin.services.notification.AdminNotificationService;
import com.serch.server.admin.services.permission.requests.PermissionRequest;
import com.serch.server.admin.services.permission.requests.PermissionScopeRequest;
import com.serch.server.admin.services.permission.responses.PermissionRequestGroupResponse;
import com.serch.server.admin.services.permission.responses.PermissionRequestResponse;
import com.serch.server.admin.services.permission.responses.PermissionResponse;
import com.serch.server.admin.services.permission.responses.PermissionScopeResponse;
import com.serch.server.admin.services.scopes.admin.requests.UpdatePermissionRequest;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.utils.ActivityUtils;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionImplementation implements PermissionService {
    private final AdminActivityService activityService;
    private final AdminNotificationService notificationService;
    private final GrantedPermissionScopeRepository grantedPermissionScopeRepository;
    private final GrantedPermissionRepository grantedPermissionRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final AdminRepository adminRepository;
    private final RequestedPermissionRepository requestedPermissionRepository;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public GrantedPermissionScope create(PermissionScope scope, Admin admin, String account) {
        assert scope != null;

        if(account != null) {
            assert scope == PermissionScope.INDIVIDUAL;
            Optional<GrantedPermissionScope> existing = grantedPermissionScopeRepository.findByAccountAndAdmin_Id(account, admin.getId());
            if(existing.isPresent()) {
                return existing.get();
            }
        } else {
            Optional<GrantedPermissionScope> existing = grantedPermissionScopeRepository.findByScopeAndAdmin_Id(scope, admin.getId());
            if(existing.isPresent()) {
                return existing.get();
            }
        }

        GrantedPermissionScope granted = new GrantedPermissionScope();
        granted.setScope(scope);
        granted.setAdmin(admin);
        if(account != null) {
            granted.setAccount(account);
        }
        return grantedPermissionScopeRepository.save(granted);
    }

    @Override
    @Transactional
    public GrantedPermission create(GrantedPermissionScope scope, Permission permission) {
        Optional<GrantedPermission> existing = grantedPermissionRepository.findByScope_IdAndPermission(scope.getId(), permission);
        if(existing.isPresent()) {
            return existing.get();
        } else {
            GrantedPermission grantedPermission = new GrantedPermission();
            grantedPermission.setScope(scope);
            grantedPermission.setPermission(permission);
            return grantedPermissionRepository.save(grantedPermission);
        }
    }

    @Override
    @Transactional
    public void attach(Admin parent, Admin child) {
        if (parent.getScopes() == null || parent.getScopes().isEmpty()) {
            return;
        }

        parent.getScopes().stream()
                .filter(granted -> granted.getScope() != PermissionScope.INDIVIDUAL)
                .forEach(granted -> {
                    GrantedPermissionScope scope = create(granted.getScope(), child, null);
                    if (granted.getPermissions() != null && !granted.getPermissions().isEmpty()) {
                        granted.getPermissions().forEach(permission -> create(scope, permission.getPermission()));
                    }
                });
    }

    @Override
    @Transactional
    public void create(List<PermissionScopeRequest> scopes, Admin admin) {
        if (scopes == null || scopes.isEmpty()) {
            return;
        }

        scopes.forEach(scopeRequest -> {
            if (scopeRequest.getPermissions() != null && !scopeRequest.getPermissions().isEmpty()) {
                if(scopeRequest.getAccount() != null && !scopeRequest.getAccount().isEmpty()) {
                    GrantedPermissionScope scope = create(scopeRequest.getScope(), admin, scopeRequest.getAccount());
                    scopeRequest.getPermissions().forEach(permission -> create(scope, permission));
                } else {
                    GrantedPermissionScope scope = create(scopeRequest.getScope(), admin, null);
                    scopeRequest.getPermissions().forEach(permission -> create(scope, permission));
                }
            }
        });
    }

    @Override
    @Transactional
    public List<PermissionScopeResponse> getScopes(Admin admin, PermissionType type) {
        if(admin.getScopes() != null && !admin.getScopes().isEmpty()) {
            if(type == PermissionType.CLUSTER) {
                return admin.getScopes().stream().filter(scope -> scope.getScope() != PermissionScope.INDIVIDUAL)
                        .map(scope -> {
                            PermissionScopeResponse response = AdminMapper.instance.response(scope);
                            response.setCreatedAt(TimeUtil.formatDay(scope.getCreatedAt()));
                            response.setUpdatedAt(TimeUtil.formatDay(scope.getUpdatedAt()));
                            return getGrantedResponse(scope, response);
                        }).toList();
            }
            return admin.getScopes().stream().filter(scope -> scope.getScope() == PermissionScope.INDIVIDUAL)
                    .map(scope -> {
                        PermissionScopeResponse response = AdminMapper.instance.response(scope);
                        response.setCreatedAt(TimeUtil.formatDay(scope.getCreatedAt()));
                        response.setUpdatedAt(TimeUtil.formatDay(scope.getUpdatedAt()));
                        try {
                            String name = userRepository.findById(UUID.fromString(scope.getAccount()))
                                    .map(User::getFullName)
                                    .orElse("");
                            response.setName(name);
                        } catch (Exception e) {
                            String name = guestRepository.findById(scope.getAccount())
                                    .map(Guest::getFullName)
                                    .orElse("");
                            response.setName(name);
                        }
                        return getGrantedResponse(scope, response);
                    }).toList();
        }
        return List.of();
    }

    private PermissionScopeResponse getGrantedResponse(GrantedPermissionScope scope, PermissionScopeResponse response) {
        response.setPermissions(scope.getPermissions().stream().map(permission -> {
            PermissionResponse permit = new PermissionResponse();
            permit.setPermission(permission.getPermission());
            permit.setId(permission.getId());
            permit.setCreatedAt(TimeUtil.formatDay(permission.getCreatedAt()));
            permit.setUpdatedAt(TimeUtil.formatDay(permission.getUpdatedAt()));
            return permit;
        }).toList());
        return response;
    }

    private void request(List<PermissionScopeRequest> scopes, Admin admin) {
        if (scopes == null || scopes.isEmpty()) {
            return;
        }

        scopes.forEach(scope -> {
            if (scope.getPermissions() != null && !scope.getPermissions().isEmpty()) {
                scope.getPermissions().forEach(permission -> {
                    RequestedPermission request = new RequestedPermission();
                    request.setAccount(scope.getAccount());
                    request.setScope(scope.getScope());
                    request.setPermission(permission);
                    request.setAdmin(admin);
                    RequestedPermission saved = requestedPermissionRepository.save(request);
                    activityService.create(ActivityUtils.getRequest(permission), null, scope.getAccount(), admin);
                    notificationService.create(
                            String.format(
                                    "%s requested for %s permission for account %s",
                                    admin.getUser().getFullName(),
                                    permission,
                                    scope.getAccount() != null ? scope.getAccount() : scope.getScope()
                            ),
                            String.valueOf(saved.getId()),
                            AdminNotificationType.PERMISSION_REQUEST,
                            admin.getUser()
                    );
                });
            }
        });
    }

    @Override
    @Transactional
    public ApiResponse<String> request(PermissionRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(request.getCluster() != null && !request.getCluster().isEmpty()) {
            request(request.getCluster(), admin);
        }
        if(request.getSpecific() != null && !request.getSpecific().isEmpty()) {
            request(request.getSpecific(), admin);
        }
        return new ApiResponse<>("Your permission requests are being evaluated. Wait a moment", HttpStatus.OK);
    }

    private boolean canAct(RequestedPermission permission) {
        Admin admin = adminRepository.findByUser_Role(Role.SUPER_ADMIN)
                .orElseThrow(() -> new PermissionException("Super admin not found"));

        User user = userUtil.getUser();
        return (user.isAdmin() && user.isUser(permission.getAdmin().getAdmin().getId()))
                || user.isUser(admin.getId());
    }

    @Override
    @Transactional
    public ApiResponse<String> grant(Long id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new PermissionException("Admin not found"));
        RequestedPermission requested = requestedPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found"));
        if(canAct(requested)) {
            requested.setStatus(PermissionStatus.APPROVED);
            requested.setUpdatedBy(admin);
            requested.setUpdatedAt(LocalDateTime.now());
            requestedPermissionRepository.save(requested);

            GrantedPermissionScope scope = create(requested.getScope(), requested.getAdmin(), requested.getAccount());
            create(scope, requested.getPermission());
            activityService.create(
                    ActivityUtils.getGrant(requested.getPermission()),
                    requested.getAdmin().getPass(),
                    scope.getAccount(),
                    admin
            );
            notificationService.create(
                    String.format(
                            "%s granted %s permission for account %s to %s",
                            admin.getUser().getFullName(),
                            requested.getPermission(),
                            scope.getAccount() != null ? scope.getAccount() : scope.getScope(),
                            requested.getAdmin().getUser().getFullName()
                    ),
                    String.valueOf(requested.getId()),
                    AdminNotificationType.DEFAULT,
                    admin.getUser()
            );
            return new ApiResponse<>("Permission granted", HttpStatus.OK);
        } else {
            throw new PermissionException("Access denied. Cannot perform this action");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> decline(Long id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new PermissionException("Admin not found"));
        RequestedPermission requested = requestedPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found"));
        if(canAct(requested)) {
            requested.setStatus(PermissionStatus.REJECTED);
            requested.setUpdatedBy(admin);
            requested.setUpdatedAt(LocalDateTime.now());
            requestedPermissionRepository.save(requested);

            activityService.create(
                    ActivityUtils.getDecline(requested.getPermission()),
                    requested.getAdmin().getPass(),
                    requested.getAccount(),
                    admin
            );
            notificationService.create(
                    String.format(
                            "%s declined %s permission for account %s to %s",
                            admin.getUser().getFullName(),
                            requested.getPermission(),
                            requested.getAccount() != null ? requested.getAccount() : requested.getScope(),
                            requested.getAdmin().getUser().getFullName()
                    ),
                    String.valueOf(requested.getId()),
                    AdminNotificationType.DEFAULT,
                    admin.getUser()
            );
            return new ApiResponse<>("Permission revoked", HttpStatus.OK);
        } else {
            throw new PermissionException("Access denied. Cannot perform this action");
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<PermissionRequestGroupResponse>> requests() {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new PermissionException("Admin not found"));
        if(admin.isSuper()) {
            List<RequestedPermission> permissions = requestedPermissionRepository.findAll();
            return requestResponse(permissions);
        } else {
            List<RequestedPermission> permissions = requestedPermissionRepository.findByAdmin_Admin_Id(admin.getId());
            return requestResponse(permissions);
        }
    }

    private ApiResponse<List<PermissionRequestGroupResponse>> requestResponse(List<RequestedPermission> permissions) {
        if(permissions == null || permissions.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            Map<LocalDate, List<RequestedPermission>> permitted = permissions.stream()
                    .collect(Collectors.groupingBy(permission -> permission.getCreatedAt().toLocalDate()));
            List<PermissionRequestGroupResponse> response = new ArrayList<>();
            permitted.forEach((date, permit) -> {
                PermissionRequestGroupResponse group = new PermissionRequestGroupResponse();
                group.setCreatedAt(LocalDateTime.of(date, LocalTime.now()));
                group.setLabel(TimeUtil.formatDay(LocalDateTime.of(date, LocalTime.now())));
                group.setRequests(permissions.stream().map(permission -> {
                    PermissionRequestResponse request = AdminMapper.instance.response(permission);
                    request.setMessage(String.format(
                            "%s requested for %s permission for account %s",
                            permission.getAdmin().getUser().getFullName(),
                            permission,
                            permission.getAccount() != null ? permission.getAccount() : permission.getScope()
                    ));
                    request.setLabel(TimeUtil.formatTime(permission.getCreatedAt()));
                    return request;
                }).toList());
                response.add(group);
            });
            return new ApiResponse<>(response);
        }
    }

    @Transactional
    public ApiResponse<String> updatePermissions(UpdatePermissionRequest request) {
        // Retrieve the current admin from the database
        Admin admin = adminRepository.findById(request.getId())
                .orElseThrow(() -> new PermissionException("Admin not found"));

        // Retrieve current scopes
        List<GrantedPermissionScope> cluster = admin.getScopes().stream()
                .filter(scope -> scope.getScope() != PermissionScope.INDIVIDUAL)
                .collect(Collectors.toList());

        List<GrantedPermissionScope> specific = admin.getScopes().stream()
                .filter(scope -> scope.getScope() == PermissionScope.INDIVIDUAL)
                .collect(Collectors.toList());

        // Compare and update cluster scopes
        updateScopes(cluster, request.getCluster(), admin);

        // Compare and update individual scopes
        updateScopes(specific, request.getSpecific(), admin);

        // Save updated admin
        adminRepository.save(admin);

        return new ApiResponse<>("Permissions updated successfully");
    }

    @Transactional
    protected void updateScopes(List<GrantedPermissionScope> current, List<PermissionScopeResponse> newScopes, Admin user) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        // Convert current to a map for easy lookup
        Map<Long, GrantedPermissionScope> currentMap = current.stream()
                .collect(Collectors.toMap(GrantedPermissionScope::getId, scope -> scope));

        // Handle additions and updates
        for (PermissionScopeResponse newScope : newScopes) {
            if (newScope.getId() == null) {
                // New scope, add to database
                GrantedPermissionScope scope = create(newScope.getScope(), user, newScope.getAccount());
                newScope.getPermissions().forEach(permission -> {
                    GrantedPermission granted = create(scope, permission.getPermission());
                    activityService.create(
                            ActivityUtils.getGrant(permission.getPermission()),
                            user.getPass(), scope.getAccount(), admin
                    );
                    notificationService.create(
                            String.format(
                                    "%s granted %s permission for account %s to %s",
                                    admin.getUser().getFullName(),
                                    permission.getPermission(),
                                    scope.getAccount() != null ? scope.getAccount() : scope.getScope(),
                                    user.getUser().getFullName()
                            ),
                            String.valueOf(granted.getId()),
                            AdminNotificationType.DEFAULT,
                            user.getUser()
                    );
                });
                current.add(scope);
            } else {
                // Existing scope, update if needed
                GrantedPermissionScope currentScope = currentMap.get(newScope.getId());
                if (currentScope != null) {
                    updateScopePermissions(currentScope, newScope.getPermissions());
                    currentMap.remove(newScope.getId());
                }
            }
        }

        // Handle deletions
        for (GrantedPermissionScope currentScope : currentMap.values()) {
            grantedPermissionScopeRepository.delete(currentScope);
            current.remove(currentScope);
        }
    }

    @Transactional
    protected void updateScopePermissions(GrantedPermissionScope scope, List<PermissionResponse> newPermissions) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        Map<Long, GrantedPermission> currentMap = scope.getPermissions().stream()
                .collect(Collectors.toMap(GrantedPermission::getId, permission -> permission));

        // Handle additions and updates
        for (PermissionResponse permission : newPermissions) {
            GrantedPermission granted;
            if (permission.getId() == null) {
                // New permission, add to database
                granted = create(scope, permission.getPermission());
            } else {
                // Existing permission, update if needed
                GrantedPermission currentPermission = currentMap.get(permission.getId());
                if (currentPermission != null) {
                    currentPermission.setPermission(permission.getPermission());
                    currentPermission.setUpdatedAt(LocalDateTime.now());
                    granted = grantedPermissionRepository.save(currentPermission);
                    currentMap.remove(permission.getId());
                } else {
                    granted = create(scope, permission.getPermission());
                }
            }
            activityService.create(
                    ActivityUtils.getAdd(permission.getPermission()),
                    scope.getAdmin().getPass(), scope.getAccount(), admin
            );
            notificationService.create(
                    String.format(
                            "%s granted %s permission for account %s to %s",
                            admin.getUser().getFullName(),
                            permission.getPermission(),
                            scope.getAccount() != null ? scope.getAccount() : scope.getScope(),
                            scope.getAdmin().getUser().getFullName()
                    ),
                    String.valueOf(granted.getId()),
                    AdminNotificationType.DEFAULT,
                    scope.getAdmin().getUser()
            );
        }

        // Handle deletions
        for (GrantedPermission currentPermission : currentMap.values()) {
            grantedPermissionRepository.delete(currentPermission);
            scope.getPermissions().remove(currentPermission);
        }
    }
}