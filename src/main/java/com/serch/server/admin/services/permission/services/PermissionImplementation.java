package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.enums.*;
import com.serch.server.admin.exceptions.PermissionException;
import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.*;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.repositories.GrantedPermissionRepository;
import com.serch.server.admin.repositories.GrantedPermissionScopeRepository;
import com.serch.server.admin.repositories.RequestedPermissionRepository;
import com.serch.server.admin.services.account.responses.AdminTeamResponse;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.admin.services.account.services.AdminProfileService;
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

import java.time.*;
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
    private final AdminProfileService adminProfileService;

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
    public ApiResponse<String> request(PermissionRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        if(request.getCluster() != null) {
            validate(List.of(request.getCluster()), admin, null);
            request(List.of(request.getCluster()), admin, null);
        }

        if(request.getSpecific() != null) {
            validate(request.getSpecific().getScopes(), admin, request.getSpecific().getAccount());
            request(request.getSpecific().getScopes(), admin, request.getSpecific().getAccount());
        }

        return new ApiResponse<>("Your permission requests are being evaluated. Wait a moment", HttpStatus.OK);
    }

    private void validate(List<PermissionScopeRequest> scopes, Admin admin, String account) {
        scopes.stream()
                .filter(scope -> scope.getPermissions() != null && !scope.getPermissions().isEmpty())
                .flatMap(scope -> scope.getPermissions().stream().map(permission -> new AbstractMap.SimpleEntry<>(scope, permission)))
                .forEach(entry -> {
                    PermissionScopeRequest scope = entry.getKey();
                    PermissionRequestDto permission = entry.getValue();

                    grantedPermissionRepository.findExisting(permission.getPermission(), scope.getScope(), admin.getId(), account)
                            .ifPresent(grantedPermission -> {
                                throw new PermissionException(String.format(
                                        "The %s permission for %s scope is already granted to you",
                                        permission.getPermission().name(),
                                        scope.getScope().getScope()
                                ));
                            });
                });
    }


    private void request(List<PermissionScopeRequest> scopes, Admin admin, String account) {
        if (scopes == null || scopes.isEmpty()) {
            return;
        }

        scopes.stream()
                .filter(scope -> scope.getPermissions() != null && !scope.getPermissions().isEmpty())
                .flatMap(scope -> scope.getPermissions().stream().map(permission -> new AbstractMap.SimpleEntry<>(scope, permission)))
                .forEach(entry -> {
                    PermissionScopeRequest scope = entry.getKey();
                    PermissionRequestDto permission = entry.getValue();

                    if (permission.getReason() == null || permission.getReason().isEmpty() || permission.getReason().length() < 3) {
                        throw new PermissionException(String.format("Reason for requesting %s permission is necessary and vital", permission.getPermission()));
                    }

                    RequestedPermission request = new RequestedPermission();
                    request.setScope(scope.getScope());
                    request.setPermission(permission.getPermission());
                    request.setRequestedBy(admin);
                    request.setReason(permission.getReason());

                    if (account != null) {
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

    private boolean canAct(RequestedPermission permission) {
        Admin admin = adminRepository.findByUser_Role(Role.SUPER_ADMIN)
                .orElseThrow(() -> new PermissionException("Super admin not found"));

        User user = userUtil.getUser();
        return (user.isAdmin() && user.isUser(permission.getRequestedBy().getAdmin().getId())) || user.isUser(admin.getId());
    }

    @Override
    @Transactional
    public ApiResponse<List<PermissionRequestGroupResponse>> grant(Long id, String expiration) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new PermissionException("Admin not found"));
        RequestedPermission requested = requestedPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found"));

        if (expiration != null && !expiration.isEmpty()) {
            validateExpirationTime(expiration, admin.getUser().getTimezone());
            requested.setExpirationPeriod(parseExpirationString(expiration, admin.getUser().getTimezone()));
        }

        if(canAct(requested)) {
            requested.setStatus(PermissionStatus.APPROVED);
            requested.setUpdatedBy(admin);
            requested.setUpdatedAt(TimeUtil.now());
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

    private ZonedDateTime parseExpirationString(String expiration, String timezone) {
        // Parse the expiration string as an Instant
        Instant instant = Instant.parse(expiration);
        // Convert the Instant to ZonedDateTime using the specified timezone
        return instant.atZone(TimeUtil.zoneId(timezone));
    }

    public void validateExpirationTime(String expiration, String timezone) {
        ZonedDateTime expirationTime = parseExpirationString(expiration, timezone);
        ZonedDateTime now = ZonedDateTime.now(TimeUtil.zoneId(timezone));

        if (expirationTime.isBefore(now) || expirationTime.isEqual(now)) {
            throw new PermissionException("Expiration time must be in the future.");
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
            List<RequestedPermission> permissions = findPermissionsForAdminAndDescendants(admin);
            return requestResponse(permissions, admin);
        }
    }

    public List<RequestedPermission> findPermissionsForAdminAndDescendants(Admin admin) {
        Set<Admin> descendantAdmins = admin.findAllDescendantAdmins(admin);
        descendantAdmins.add(admin); // Include the original admin as well

        List<UUID> adminIds = descendantAdmins.stream()
                .map(Admin::getId)
                .collect(Collectors.toList());

        return requestedPermissionRepository.findByRequestedByIdIn(adminIds);
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
                group.setCreatedAt(TimeUtil.toZonedDate(LocalDateTime.of(date, permit.getFirst().getCreatedAt().toLocalTime()), userUtil.getUser().getTimezone()));
                group.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, permit.getFirst().getCreatedAt().toLocalTime()), userUtil.getUser().getTimezone()));

                group.setRequests(permit.stream().map(permission -> {
                    PermissionRequestResponse request = AdminMapper.instance.response(permission);
                    request.setMessage(String.format(
                            "%s requested for %s permission for account %s",
                            permission.getRequestedBy().getUser().isUser(admin.getId())
                                    ? "You"
                                    : permission.getRequestedBy().getUser().getFullName(),
                            permission.getPermission().name(),
                            permission.getAccount() != null ? permission.getAccount() : permission.getScope()
                    ));
                    request.setExpiration(permission.getExpirationTime(userUtil.getUser().getTimezone()));
                    request.setLabel(TimeUtil.formatTime(permission.getCreatedAt(), permission.getRequestedBy().getUser().getTimezone()));

                    if(permission.getAccount() != null && !permission.getAccount().isEmpty()) {
                        PermissionRequestAccountDetails account = new PermissionRequestAccountDetails();
                        account.setAccount(permission.getAccount());
                        account.setName(notificationRepository.getName(permission.getAccount()));
                        account.setRole(notificationRepository.getRole(permission.getAccount()));
                    }

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

        if(permission.getRequestedBy().getUser().isUser(admin.getId()) && permission.isPending()) {
            requestedPermissionRepository.delete(permission);

            return new ApiResponse<>("Granted permission is now removed", requests().getData(), HttpStatus.OK);
        } else {
            throw new PermissionException("Cannot proceed with action. You do not have the right access to do this");
        }
    }

    @Transactional
    public ApiResponse<AdminTeamResponse> updatePermissions(UpdatePermissionRequest request) {
        // Retrieve the current admin from the database
        Admin admin = adminRepository.findById(request.getId())
                .orElseThrow(() -> new PermissionException("Admin not found"));

        if(request.getAccount() == null || request.getAccount().isEmpty()) {
            // Retrieve current scopes
            List<GrantedPermissionScope> cluster = admin.getScopes().stream()
                    .filter(scope -> scope.getAccount() == null)
                    .collect(Collectors.toList());

            // Compare and update cluster scopes
            updateScopes(cluster, request.getScopes(), admin, null);
        } else {
            List<GrantedPermissionScope> specific = admin.getScopes().stream()
                    .filter(scope -> scope.getAccount() != null)
                    .collect(Collectors.toList());

            // Compare and update individual scopes
            updateScopes(specific, request.getScopes(), admin, request.getAccount());
        }

        // Save updated admin
        adminRepository.save(admin);

        return new ApiResponse<>("Permissions updated successfully", adminProfileService.team(admin), HttpStatus.OK);
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
                    handlePermissionUpdateIfRequested(scope, admin, granted);

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
            handleScopeDeletion(currentScope.getId());
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

            handlePermissionUpdateIfRequested(scope, admin, granted);

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
            handlePermissionRevokingIfRequested(scope, admin, currentPermission);

            scope.getPermissions().remove(currentPermission);
            grantedPermissionRepository.delete(currentPermission);
            grantedPermissionRepository.flush();
        }
    }

    private void handlePermissionRevokingIfRequested(GrantedPermissionScope scope, Admin admin, GrantedPermission currentPermission) {
        List<RequestedPermission> existing = requestedPermissionRepository
                .findExisting(scope.getAdmin().getId(), scope.getScope(), scope.getAccount(), currentPermission.getPermission());
        if(existing != null && !existing.isEmpty()) {
            existing.forEach(perm -> {
                if(perm.isPending() || perm.isGranted()) {
                    perm.setStatus(PermissionStatus.REVOKED);
                    perm.setUpdatedBy(admin);
                    perm.setUpdatedAt(TimeUtil.now());
                    requestedPermissionRepository.save(perm);
                }
            });
        }
    }

    private void handlePermissionUpdateIfRequested(GrantedPermissionScope scope, Admin admin, GrantedPermission granted) {
        List<RequestedPermission> existing = requestedPermissionRepository
                .findExisting(scope.getAdmin().getId(), scope.getScope(), scope.getAccount(), granted.getPermission());
        if(existing != null && !existing.isEmpty()) {
            existing.forEach(perm -> {
                if(perm.isPending()) {
                    perm.setStatus(PermissionStatus.APPROVED);
                    perm.setUpdatedBy(admin);
                    perm.setUpdatedAt(TimeUtil.now());
                    requestedPermissionRepository.save(perm);
                }
            });
        }
    }

    @Override
    public ApiResponse<List<PermissionScopeResponse>> getAllScopes() {
        List<PermissionScopeResponse> list = Arrays.stream(PermissionScope.values()).map(scope -> {
            PermissionScopeResponse response = new PermissionScopeResponse();
            response.setScope(scope);
            response.setName(scope.getTitle());
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
        requestedPermissionRepository.findGrantedPermissionsWithExpirationTime().forEach(this::revokePermission);
    }

    @Transactional
    protected void revokePermission(RequestedPermission permission) {
        if(permission.isExpired(permission.getRequestedBy().getUser().getTimezone())) {
            revoke(permission, null);
        }
    }

    @Transactional
    protected void revoke(RequestedPermission permission, Admin admin) {
        grantedPermissionRepository.findExisting(permission.getPermission(), permission.getScope(), permission.getRequestedBy().getId(), permission.getAccount())
                .ifPresent(grantedPermission -> {
                    Long id = grantedPermission.getScope().getId();

                    // Remove the grantedPermission from the GrantedPermissionScope's permissions set
                    GrantedPermissionScope scope = grantedPermission.getScope();
                    scope.getPermissions().remove(grantedPermission);

                    // Delete the grantedPermission
                    grantedPermissionRepository.delete(grantedPermission);
                    grantedPermissionRepository.flush(); // Force flush to ensure the deletion takes effect

                    // Now handle the grantedPermissionScope deletion
                    handleScopeDeletion(id);
                });

        permission.setStatus(PermissionStatus.REVOKED);
        permission.setUpdatedAt(TimeUtil.now());

        if(admin != null) {
            permission.setUpdatedBy(admin);
        }
        requestedPermissionRepository.save(permission);
    }

    private void handleScopeDeletion(Long id) {
        grantedPermissionScopeRepository.findById(id)
                .ifPresent(grantedPermissionScope -> {
                    if (grantedPermissionScope.getPermissions() == null || grantedPermissionScope.getPermissions().isEmpty()) {
                        grantedPermissionScopeRepository.delete(grantedPermissionScope);
                        grantedPermissionScopeRepository.flush(); // Flush here as well
                    }
                });
    }
}