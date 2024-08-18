package com.serch.server.admin.services.account.services;

import com.serch.server.admin.enums.PermissionType;
import com.serch.server.admin.enums.UserAction;
import com.serch.server.admin.exceptions.AdminException;
import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import com.serch.server.admin.services.account.responses.AdminResponse;
import com.serch.server.admin.services.account.responses.AdminTeamResponse;
import com.serch.server.admin.services.permission.services.PermissionService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.core.storage.requests.FileUploadRequest;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminProfileImplementation implements AdminProfileService {
    private final StorageService supabase;
    private final PermissionService permissionService;
    private final AdminActivityService activityService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse<AdminResponse> get() {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        return new ApiResponse<>(prepare(admin));
    }

    private AdminResponse prepare(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setProfile(profile(admin));
        response.setTeam(team(admin));
        response.setActivities(activityService.activities(admin.getId()));
        return response;
    }

    @Override
    @Transactional
    public ApiResponse<AdminResponse> update(AdminProfileUpdateRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(!Objects.equals(admin.getUser().getFirstName(), request.getFirstName()) && !request.getFirstName().isEmpty()) {
            admin.getUser().setFirstName(request.getFirstName());
            admin.getUser().setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin.getUser());
        }
        if(!Objects.equals(admin.getUser().getLastName(), request.getLastName()) && !request.getLastName().isEmpty()) {
            admin.getUser().setLastName(request.getLastName());
            admin.getUser().setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin.getUser());
        }
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
            admin.setUpdatedAt(LocalDateTime.now());
            adminRepository.save(admin);
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
        response.setProfileUpdatedAt(TimeUtil.formatDay(admin.getUpdatedAt()));
        response.setProfileCreatedAt(TimeUtil.formatDay(admin.getCreatedAt()));
        response.setAccountCreatedAt(TimeUtil.formatDay(admin.getUser().getCreatedAt()));
        response.setAccountUpdatedAt(TimeUtil.formatDay(admin.getUser().getUpdatedAt()));
        response.setEmailConfirmedAt(TimeUtil.formatDay(admin.getUser().getEmailConfirmedAt()));
        response.setPasswordUpdatedAt(TimeUtil.formatDay(admin.getUser().getLastUpdatedAt()));
        response.setLastSignedIn(TimeUtil.formatLastSignedIn(admin.getUser().getLastSignedIn(), false));
        return response;
    }

    @Override
    @Transactional
    public AdminTeamResponse team(Admin admin) {
        AdminTeamResponse response = new AdminTeamResponse();
        response.setPosition(admin.getPosition());
        response.setDepartment(admin.getDepartment());
        response.setRole(admin.getUser().getRole());
        response.setSpecific(permissionService.getScopes(admin, PermissionType.SPECIFIC));
        response.setCluster(permissionService.getScopes(admin, PermissionType.CLUSTER));
        return response;
    }
}
