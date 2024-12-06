package com.serch.server.admin.services.account.services;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.enums.UserAction;
import com.serch.server.admin.exceptions.AdminException;
import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import com.serch.server.admin.services.account.responses.AdminResponse;
import com.serch.server.admin.services.permission.services.GrantedPermissionService;
import com.serch.server.admin.services.responses.AccountScopeDetailResponse;
import com.serch.server.admin.services.team.responses.AdminTeamResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.core.storage.requests.FileUploadRequest;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminProfileImplementation implements AdminProfileService {
    private final StorageService supabase;
    private final GrantedPermissionService permissionService;
    private final AdminActivityService activityService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public ApiResponse<AdminResponse> get() {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        return new ApiResponse<>(prepare(admin));
    }

    @Override
    @Transactional
    public AdminResponse prepare(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setProfile(profile(admin));
        response.setTeam(team(admin));
        response.setActivities(activityService.activities(admin.getId(), null, null));
        return response;
    }

    @Override
    @Transactional
    public ApiResponse<AdminResponse> update(AdminProfileUpdateRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(!Objects.equals(admin.getUser().getFirstName(), request.getFirstName()) && !request.getFirstName().isEmpty()) {
            admin.getUser().setFirstName(request.getFirstName());
            admin.getUser().setUpdatedAt(TimeUtil.now());
            userRepository.save(admin.getUser());
        }
        if(!Objects.equals(admin.getUser().getLastName(), request.getLastName()) && !request.getLastName().isEmpty()) {
            admin.getUser().setLastName(request.getLastName());
            admin.getUser().setUpdatedAt(TimeUtil.now());
            userRepository.save(admin.getUser());
        }

        activityService.create(ActivityMode.PROFILE_UPDATE, null, null, admin);
        return new ApiResponse<>("Profile updated", prepare(admin), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse<AdminResponse> uploadAvatar(FileUploadRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(HelperUtil.isUploadEmpty(request)) {
            throw new AdminException("There is no upload data");
        } else {
            String url = supabase.upload(request, "admin");
            admin.setAvatar(url);
            admin.setUpdatedAt(TimeUtil.now());
            adminRepository.save(admin);

            activityService.create(ActivityMode.AVATAR_CHANGE, null, null, admin);
            return new ApiResponse<>("Avatar uploaded", prepare(admin), HttpStatus.OK);
        }
    }

    @Override
    @Transactional
    public AdminProfileResponse profile(Admin admin) {
        AdminProfileResponse response = AdminMapper.instance.response(admin.getUser());
        response.setAvatar(admin.getAvatar());
        response.setEmpId(admin.getPass());
        response.setShouldResendInvite(admin.getUser().getAction() == UserAction.INVITE);

        return updateProfile(admin.getCreatedAt(), admin.getUpdatedAt(), admin.getUser(), response);
    }

    @Override
    @Transactional
    public <T extends AccountScopeDetailResponse> T updateProfile(ZonedDateTime updatedAt, ZonedDateTime createdAt, User user, T response) {
        response.setProfileUpdatedAt(TimeUtil.formatDay(updatedAt, userUtil.getUser().getTimezone()));
        response.setProfileCreatedAt(TimeUtil.formatDay(createdAt, userUtil.getUser().getTimezone()));
        response.setAccountCreatedAt(TimeUtil.formatDay(user.getCreatedAt(), userUtil.getUser().getTimezone()));
        response.setAccountUpdatedAt(TimeUtil.formatDay(user.getUpdatedAt(), userUtil.getUser().getTimezone()));
        response.setEmailConfirmedAt(TimeUtil.formatDay(user.getEmailConfirmedAt(), userUtil.getUser().getTimezone()));
        response.setPasswordUpdatedAt(TimeUtil.formatDay(user.getLastUpdatedAt(), userUtil.getUser().getTimezone()));
        response.setLastSignedIn(TimeUtil.formatLastSignedIn(user.getLastSignedIn(), userUtil.getUser().getTimezone(), false));

        return response;
    }

    @Override
    @Transactional
    public AdminTeamResponse team(Admin admin) {
        AdminTeamResponse response = new AdminTeamResponse();
        response.setPosition(admin.getPosition());
        response.setDepartment(admin.getDepartment());
        response.setRole(admin.getUser().getRole());
        response.setSpecific(permissionService.getGrantedSpecificPermissions(admin));
        response.setCluster(permissionService.getGrantedClusterPermissions(admin));
        return response;
    }

    @Override
    public ApiResponse<String> updateTimezone(String timezone) {
        adminRepository.findById(userUtil.getUser().getId())
                .ifPresent(profile -> {
                    profile.getUser().setTimezone(timezone);
                    userRepository.save(profile.getUser());
                });

        return new ApiResponse<>("Successfully updated timezone", HttpStatus.OK);
    }
}
