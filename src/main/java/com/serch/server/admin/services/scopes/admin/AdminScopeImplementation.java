package com.serch.server.admin.services.scopes.admin;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.exceptions.AdminException;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.admin.services.account.services.AdminProfileService;
import com.serch.server.admin.services.account.services.TeamService;
import com.serch.server.admin.services.responses.AnalysisResponse;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.admin.requests.ChangeRoleRequest;
import com.serch.server.admin.services.scopes.admin.requests.ChangeStatusRequest;
import com.serch.server.admin.services.scopes.admin.responses.AdminScopeResponse;
import com.serch.server.admin.services.scopes.common.CommonAccountAnalysisService;
import com.serch.server.admin.services.scopes.common.CommonAuthService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.core.storage.requests.FileUploadRequest;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminScopeImplementation implements AdminScopeService {
    private final AdminActivityService activityService;
    private final AdminProfileService profileService;
    private final CommonAuthService authService;
    private final StorageService supabase;
    private final TeamService teamService;
    private final CommonAccountAnalysisService analysisService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<AdminScopeResponse> fetch(UUID id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AdminException("Admin not found"));
        AdminScopeResponse response = new AdminScopeResponse();
        response.setProfile(profileService.profile(admin));
        response.setTeam(profileService.team(admin));
        response.setActivities(activityService.activities(admin.getId()));
        response.setMfa(authService.mfa(admin.getUser()));
        response.setAuth(authService.auth(admin.getUser()));
        response.setChallenges(authService.challenges(admin.getUser()));
        response.setSessions(authService.sessions(admin.getUser()));
        response.setStructure(teamService.team());

        AnalysisResponse analysisResponse = new AnalysisResponse();
        analysisResponse.setAuth(analysisService.auth(admin.getUser(), null));
        analysisResponse.setStatus(analysisService.accountStatus(admin.getUser(), null));
        analysisResponse.setYears(analysisService.years(admin.getUser()));
        response.setAnalysis(analysisResponse);

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<List<ChartMetric>> fetchAuthChart(UUID id, Integer year) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AdminException("Admin not found"));
        return new ApiResponse<>(analysisService.auth(admin.getUser(), year));
    }

    @Override
    public ApiResponse<List<ChartMetric>> fetchAccountStatusChart(UUID id, Integer year) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AdminException("Admin not found"));
        return new ApiResponse<>(analysisService.accountStatus(admin.getUser(), year));
    }

    @Override
    public ApiResponse<String> changeAvatar(FileUploadRequest request, UUID id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin user = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));
        if(HelperUtil.isUploadEmpty(request)) {
            throw new AdminException("There is no upload data");
        } else {
            String url = supabase.upload(request, "user");
            user.setAvatar(url);
            user.setUpdatedAt(LocalDateTime.now());
            adminRepository.save(user);
            activityService.create(ActivityMode.AVATAR_CHANGE, user.getPass(), null, admin);
            return new ApiResponse<>("Avatar uploaded", url, HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<String> changeStatus(ChangeStatusRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin user = adminRepository.findById(request.getId()).orElseThrow(() -> new AuthException("Admin not found"));

        if(request.getStatus() != null) {
            user.getUser().setStatus(request.getStatus());
            user.getUser().setUpdatedAt(LocalDateTime.now());
            userRepository.save(user.getUser());
            activityService.create(ActivityMode.STATUS_CHANGE, user.getPass(), null, admin);
            return new ApiResponse<>("Status changed", request.getStatus().getType(), HttpStatus.OK);
        }
        throw new AdminException("Status not found");
    }

    @Override
    public ApiResponse<String> changeRole(ChangeRoleRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin user = adminRepository.findById(request.getId()).orElseThrow(() -> new AuthException("Admin not found"));

        if(request.getRole() != null && request.getRole() == Role.SUPER_ADMIN) {
            throw new AdminException("Super admin cannot be assigned");
        } else {
            if(request.getRole() != null) {
                user.getUser().setRole(request.getRole());
                user.getUser().setUpdatedAt(LocalDateTime.now());
                userRepository.save(user.getUser());
            }
            if(request.getDepartment() != null) {
                user.setDepartment(request.getDepartment());
                user.setUpdatedAt(LocalDateTime.now());
                adminRepository.save(user);
            }
            if(request.getPosition() != null) {
                user.setPosition(request.getPosition());
                user.setUpdatedAt(LocalDateTime.now());
                adminRepository.save(user);
            }
            activityService.create(ActivityMode.ROLE_CHANGE, user.getPass(), null, admin);
            return new ApiResponse<>("Role changed", HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<Boolean> toggle(UUID id) {
        Admin user = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));
        if(user.getMustHaveMFA()) {
            user.setMustHaveMFA(false);
            user.setUpdatedAt(LocalDateTime.now());
            adminRepository.save(user);
            return new ApiResponse<>("MFA constraint is now removed", false, HttpStatus.OK);
        } else {
            user.setMustHaveMFA(true);
            user.setUpdatedAt(LocalDateTime.now());
            adminRepository.save(user);
            return new ApiResponse<>("MFA constraint is now invoked", true, HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<String> update(AdminProfileUpdateRequest request, UUID id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin user = adminRepository.findById(id)
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(!Objects.equals(user.getUser().getLastName(), request.getLastName()) && !request.getLastName().isEmpty()) {
            user.getUser().setLastName(request.getLastName());
            user.getUser().setUpdatedAt(LocalDateTime.now());
            userRepository.save(user.getUser());
        }
        if(!Objects.equals(user.getUser().getFirstName(), request.getFirstName()) && !request.getFirstName().isEmpty()) {
            user.getUser().setFirstName(request.getFirstName());
            user.getUser().setUpdatedAt(LocalDateTime.now());
            userRepository.save(user.getUser());
        }
        activityService.create(ActivityMode.PROFILE_UPDATE, user.getPass(), null, admin);
        return new ApiResponse<>("Profile updated", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> delete(UUID id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin user = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));

        user.getUser().setStatus(AccountStatus.DELETED);
        user.getUser().setUpdatedAt(LocalDateTime.now());
        userRepository.save(user.getUser());
        activityService.create(ActivityMode.ACCOUNT_DELETE, user.getUser().getFullName(), null, admin);
        return new ApiResponse<>("Account deleted", HttpStatus.OK);
    }
}
