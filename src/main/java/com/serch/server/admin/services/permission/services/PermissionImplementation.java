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
import com.serch.server.admin.services.permission.requests.PermissionRequestDto;
import com.serch.server.admin.services.permission.requests.PermissionScopeRequest;
import com.serch.server.admin.services.permission.responses.*;
import com.serch.server.admin.services.permission.requests.UpdatePermissionRequest;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.utils.ActivityUtils;
import com.serch.server.utils.HelperUtil;
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
    private final INotificationRepository notificationRepository;
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

        Optional<GrantedPermissionScope> existing;
        if(account != null && !account.isEmpty()) {
            existing = grantedPermissionScopeRepository.findByAccountAndScopeAndAdmin_Id(account, scope, admin.getId());
        } else {
            existing = grantedPermissionScopeRepository.findByScopeAndAdmin_Id(scope, admin.getId());
        }

        if(existing.isPresent()) {
            return existing.get();
        }

        GrantedPermissionScope granted = new GrantedPermissionScope();
        granted.setScope(scope);
        granted.setAdmin(admin);
        if(account != null && !account.isEmpty()) {
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
                .filter(granted -> granted.getAccount() == null)
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
                GrantedPermissionScope scope = create(scopeRequest.getScope(), admin, null);
                scopeRequest.getPermissions().forEach(permission -> create(scope, permission.getPermission()));
            }
        });
    }

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

    private void request(List<PermissionScopeRequest> scopes, Admin admin, String account) {
        if (scopes == null || scopes.isEmpty()) {
            return;
        }

        scopes.forEach(scope -> {
            if (scope.getPermissions() != null && !scope.getPermissions().isEmpty()) {
                scope.getPermissions().forEach(permission -> {
                    RequestedPermission request = new RequestedPermission();
                    request.setScope(scope.getScope());
                    request.setPermission(permission.getPermission());
                    request.setRequestedBy(admin);

                    if(account != null) {
                        request.setAccount(account);
                    }

                    RequestedPermission saved = requestedPermissionRepository.save(request);
                    activityService.create(ActivityUtils.getRequest(permission.getPermission()), null, account, admin);

                    notificationService.create(
                            String.format(
                                    "%s requested for %s permission for account %s",
                                    admin.getUser().getFullName(),
                                    permission,
                                    account != null ? account : scope.getScope()
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
            request(request.getCluster(), admin, null);
        }

        if(request.getSpecific() != null && !request.getSpecific().isEmpty()) {
            request.getSpecific().forEach(spec -> request(spec.getScopes(), admin, spec.getAccount()));
        }

        return new ApiResponse<>("Your permission requests are being evaluated. Wait a moment", HttpStatus.OK);
    }

    private boolean canAct(RequestedPermission permission) {
        Admin admin = adminRepository.findByUser_Role(Role.SUPER_ADMIN)
                .orElseThrow(() -> new PermissionException("Super admin not found"));

        User user = userUtil.getUser();
        return (user.isAdmin() && user.isUser(permission.getRequestedBy().getAdmin().getId())) || user.isUser(admin.getId());
    }

    @Override
    @Transactional
    public ApiResponse<List<PermissionRequestGroupResponse>> grant(Long id, Long expiration) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new PermissionException("Admin not found"));
        RequestedPermission requested = requestedPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found"));

        if(canAct(requested)) {
            requested.setStatus(PermissionStatus.APPROVED);
            requested.setUpdatedBy(admin);
            requested.setUpdatedAt(TimeUtil.now());
            requested.setExpirationPeriod(expiration);
            requested.setGrantedAt(TimeUtil.now());
            requestedPermissionRepository.save(requested);

            create(create(requested.getScope(), requested.getRequestedBy(), requested.getAccount()), requested.getPermission());

            activityService.create(
                    ActivityUtils.getGrant(requested.getPermission()),
                    requested.getRequestedBy().getPass(),
                    requested.getAccount(),
                    admin
            );

            notificationService.create(
                    String.format(
                            "%s granted %s permission for account %s to %s",
                            admin.getUser().getFullName(),
                            requested.getPermission(),
                            requested.getAccount() != null ? requested.getAccount() : requested.getScope(),
                            requested.getRequestedBy().getUser().getFullName()
                    ),
                    String.valueOf(requested.getId()),
                    AdminNotificationType.DEFAULT,
                    admin.getUser()
            );

            return new ApiResponse<>("Requested permission is now granted", requests().getData(), HttpStatus.OK);
        } else {
            throw new PermissionException("Access denied. Cannot perform this action");
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<PermissionRequestGroupResponse>> decline(Long id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new PermissionException("Admin not found"));
        RequestedPermission requested = requestedPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found"));

        if(canAct(requested)) {
            requested.setStatus(PermissionStatus.REJECTED);
            requested.setUpdatedBy(admin);
            requested.setUpdatedAt(TimeUtil.now());
            requestedPermissionRepository.save(requested);

            activityService.create(
                    ActivityUtils.getDecline(requested.getPermission()),
                    requested.getRequestedBy().getPass(),
                    requested.getAccount(),
                    admin
            );

            notificationService.create(
                    String.format(
                            "%s declined %s permission for account %s to %s",
                            admin.getUser().getFullName(),
                            requested.getPermission(),
                            requested.getAccount() != null ? requested.getAccount() : requested.getScope(),
                            requested.getRequestedBy().getUser().getFullName()
                    ),
                    String.valueOf(requested.getId()),
                    AdminNotificationType.DEFAULT,
                    admin.getUser()
            );

            return new ApiResponse<>("Requested permission is now declined", requests().getData(), HttpStatus.OK);
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
            return requestResponse(permissions, admin);
        } else {
            List<RequestedPermission> permissions = requestedPermissionRepository.findByAdmin_Admin_Id(admin.getId());
            return requestResponse(permissions, admin);
        }
    }

    private ApiResponse<List<PermissionRequestGroupResponse>> requestResponse(List<RequestedPermission> permissions, Admin admin) {
        if(permissions == null || permissions.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            Map<LocalDate, List<RequestedPermission>> permitted = permissions.stream()
                    .collect(Collectors.groupingBy(permission -> permission.getCreatedAt().toLocalDate()));

            List<PermissionRequestGroupResponse> response = new ArrayList<>();
            permitted.forEach((date, permit) -> {
                PermissionRequestGroupResponse group = new PermissionRequestGroupResponse();
                group.setCreatedAt(TimeUtil.toZonedDate(LocalDateTime.of(date, LocalTime.now()), userUtil.getUser().getTimezone()));
                group.setLabel(TimeUtil.formatDay(LocalDateTime.of(date, LocalTime.now()), userUtil.getUser().getTimezone()));

                group.setRequests(permissions.stream().map(permission -> {
                    PermissionRequestResponse request = AdminMapper.instance.response(permission);
                    request.setMessage(String.format(
                            "%s requested for %s permission for account %s",
                            permission.getRequestedBy().getUser().getFullName(),
                            permission,
                            permission.getAccount() != null ? permission.getAccount() : permission.getScope()
                    ));
                    request.setExpiration(permission.getExpirationTime(userUtil.getUser().getTimezone()));
                    request.setLabel(TimeUtil.formatTime(permission.getCreatedAt(), permission.getRequestedBy().getUser().getTimezone()));

                    PermissionRequestDetails details = new PermissionRequestDetails();
                    details.setGranted(TimeUtil.formatDay(permission.getGrantedAt(), admin.getUser().getTimezone()));
                    details.setName(permission.getRequestedBy() != null ? permission.getRequestedBy().getFullName() : "");
                    details.setAdmin(permission.getRequestedBy() != null ? permission.getRequestedBy().getId().toString() : "");
                    details.setUpdatedByName(permission.getUpdatedBy() != null ? permission.getUpdatedBy().getFullName() : "");
                    details.setUpdatedBy(permission.getUpdatedBy() != null ? permission.getUpdatedBy().getId().toString() : "");

                    request.setDetails(details);
                    return request;
                }).toList());
                response.add(group);
            });

            return new ApiResponse<>(response);
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<PermissionRequestGroupResponse>> revoke(Long id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        RequestedPermission permission = requestedPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found"));

        if(admin.isSuper() || admin.getUser().isUser(permission.getRequestedBy().getAdmin().getId())) {
            revoke(permission, admin);

            return new ApiResponse<>("Granted permission is now revoked", requests().getData(), HttpStatus.OK);
        } else {
            throw new PermissionException("Access denied. Cannot perform this action");
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<PermissionRequestGroupResponse>> cancel(Long id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        RequestedPermission permission = requestedPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found"));

        if(permission.getRequestedBy().getUser().isUser(admin.getId())) {
            requestedPermissionRepository.delete(permission);

            return new ApiResponse<>("Granted permission is now removed", requests().getData(), HttpStatus.OK);
        } else {
            throw new PermissionException("Cannot proceed with action. You do not have the right access to do this");
        }
    }

    @Transactional
    public ApiResponse<String> updatePermissions(UpdatePermissionRequest request) {
        // Retrieve the current admin from the database
        Admin admin = adminRepository.findById(request.getId())
                .orElseThrow(() -> new PermissionException("Admin not found"));

        // Retrieve current scopes
        List<GrantedPermissionScope> cluster = admin.getScopes().stream()
                .filter(scope -> scope.getAccount() == null)
                .collect(Collectors.toList());

        List<GrantedPermissionScope> specific = admin.getScopes().stream()
                .filter(scope -> scope.getAccount() != null)
                .collect(Collectors.toList());

        // Compare and update cluster scopes
        updateScopes(cluster, request.getCluster(), admin, null);

        // Compare and update individual scopes
        request.getSpecific().forEach(spec -> updateScopes(specific, spec.getScopes(), admin, spec.getAccount()));

        // Save updated admin
        adminRepository.save(admin);

        return new ApiResponse<>("Permissions updated successfully");
    }

    @Transactional
    protected void updateScopes(List<GrantedPermissionScope> current, List<PermissionScopeRequest> newList, Admin user, String account) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        // Convert current to a map for easy lookup
        Map<Long, GrantedPermissionScope> currentMap = current.stream()
                .collect(Collectors.toMap(GrantedPermissionScope::getId, scope -> scope));

        // Handle additions and updates
        for (PermissionScopeRequest newScope : newList) {
            if (newScope.getId() == null) {
                // New scope, add to database
                GrantedPermissionScope scope = create(newScope.getScope(), user, account);
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
                    updateScopePermissions(currentScope, newScope.getPermissions(), admin);
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
    protected void updateScopePermissions(GrantedPermissionScope scope, List<PermissionRequestDto> newList, Admin admin) {
        Map<Long, GrantedPermission> currentMap = scope.getPermissions().stream()
                .collect(Collectors.toMap(GrantedPermission::getId, permission -> permission));

        // Handle additions and updates
        for (PermissionRequestDto permission : newList) {
            GrantedPermission granted;
            if (permission.getId() == null) {
                // New permission, add to database
                granted = create(scope, permission.getPermission());
            } else {
                // Existing permission, update if needed
                GrantedPermission currentPermission = currentMap.get(permission.getId());
                if (currentPermission != null) {
                    currentPermission.setPermission(permission.getPermission());
                    currentPermission.setUpdatedAt(TimeUtil.now());
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

    @Override
    public ApiResponse<List<PermissionScopeResponse>> getAllScopes() {
        List<PermissionScopeResponse> list = Arrays.stream(PermissionScope.values()).map(scope -> {
            PermissionScopeResponse response = new PermissionScopeResponse();
            response.setScope(scope);
            response.setName(scope.getScope());
            return response;
        }).toList();

        return new ApiResponse<>(list);
    }

    @Override
    @Transactional
    public ApiResponse<PermissionAccountSearchResponse> search(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        PermissionAccountSearchResponse response = new PermissionAccountSearchResponse();

        if(uuid != null) {
            User user = userRepository.findById(uuid).orElseThrow(() -> new AuthException("Account not found"));

            if(user.isSuperAdmin()) {
                throw new PermissionException("Cannot view this profile");
            } else if(!(userUtil.getUser().isAdmin() || userUtil.getUser().isSuperAdmin()) && user.isAdmin()) {
                throw new PermissionException("You are not allowed to seek permission for an admin account");
            }

            response.setId(user.getId().toString());
            response.setRole(user.getRole().getType());
            response.setAvatar(notificationRepository.getAvatar(user.getId().toString()));
            response.setName(user.getFullName());
            response.setScopes(user.getRole().getScopes().stream().toList());
        } else {
            Guest guest = guestRepository.findById(id).orElseThrow(() -> new AuthException("Account not found"));

            response.setId(guest.getId());
            response.setRole("GUEST");
            response.setAvatar(guest.getAvatar());
            response.setName(guest.getFullName());
            response.setScopes(List.of(PermissionScope.GUEST));
        }

        return new ApiResponse<>(response);
    }

    @Override
    @Transactional
    public void revokeExpiredPermissions() {
        requestedPermissionRepository.findGrantedPermissionsWithExpirationTime().forEach(permission -> revoke(permission, null));
    }

    private void revoke(RequestedPermission permission, Admin admin) {
        if(permission.isExpired()) {
            grantedPermissionRepository.findExisting(permission.getPermission(), permission.getScope(), permission.getRequestedBy().getId(), permission.getAccount())
                    .ifPresent(grantedPermission -> {
                        grantedPermissionRepository.delete(grantedPermission);

                        grantedPermissionScopeRepository.findById(grantedPermission.getScope().getId())
                                .ifPresent(grantedPermissionScope -> {
                                    if(grantedPermissionScope.getPermissions() == null || grantedPermissionScope.getPermissions().isEmpty()) {
                                        grantedPermissionScopeRepository.delete(grantedPermissionScope);
                                    }
                                });
                    });

            permission.setStatus(PermissionStatus.REVOKED);
            permission.setUpdatedAt(TimeUtil.now());

            if(admin != null) {
                permission.setUpdatedBy(admin);
            }
            requestedPermissionRepository.save(permission);
        }
    }
}