package com.serch.server.admin.services.account.services;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.models.AdminActivity;
import com.serch.server.admin.repositories.AdminActivityRepository;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.account.responses.AdminActivityResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminActivityImplementation implements AdminActivityService {
    private final AdminActivityRepository adminActivityRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public List<AdminActivityResponse> activities(UUID id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin account = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));

        List<AdminActivity> activities = adminActivityRepository.findByAdminId(id, account.getPass());
        if(activities == null || activities.isEmpty()) {
            return new ArrayList<>();
        } else {
            return activities.stream().sorted(Comparator.comparing(AdminActivity::getCreatedAt).reversed()).map(activity -> response(
                    activity,
                    admin.getUser().getRole() != Role.TEAM,
                    admin.getId().equals(id)
            )).toList();
        }
    }

    @Override
    @Transactional
    public List<AdminActivityResponse> activities() {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        return switch (admin.getUser().getRole()) {
            case SUPER_ADMIN -> {
                List<AdminActivity> activities = adminActivityRepository.findAll();
                if(activities.isEmpty()) {
                    yield new ArrayList<>();
                } else {
                    yield activities.stream().sorted(Comparator.comparing(AdminActivity::getCreatedAt).reversed()).map(activity -> response(
                            activity,
                            admin.getUser().getRole() != Role.TEAM,
                            true
                    )).toList();
                }
            }
            case ADMIN -> {
                List<AdminActivity> activities = adminActivityRepository.findAdmin(admin.getId(), admin.getPass());
                if(activities.isEmpty()) {
                    yield new ArrayList<>();
                } else {
                    yield activities.stream().sorted(Comparator.comparing(AdminActivity::getCreatedAt).reversed()).map(activity -> response(
                            activity,
                            admin.getUser().getRole() != Role.TEAM,
                            true
                    )).toList();
                }
            }
            case MANAGER -> {
                List<AdminActivity> activities = adminActivityRepository.findManager(admin.getId(), admin.getPass());
                if(activities.isEmpty()) {
                    yield new ArrayList<>();
                } else {
                    yield activities.stream().sorted(Comparator.comparing(AdminActivity::getCreatedAt).reversed()).map(activity -> response(
                            activity,
                            admin.getUser().getRole() != Role.TEAM,
                            true
                    )).toList();
                }
            }
            default -> activities(admin.getId());
        };
    }

    private AdminActivityResponse response(AdminActivity activity, boolean showAssociated, boolean isLoggedInAdmin) {
        AdminActivityResponse response = AdminMapper.instance.response(activity);
        response.setLabel(TimeUtil.formatDay(activity.getUpdatedAt(), activity.getAdmin().getUser().getTimezone()));
        response.setActivity(activity(isLoggedInAdmin, activity));
        if(!showAssociated) {
            response.setAssociated("");
        }
        response.setHeader(activity.getMode().getValue());
        return response;
    }

    private String activity(boolean isLoggedInAdmin, AdminActivity activity) {
        Admin loggedInAdmin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        String name = activity.getAdmin().getUser().getFullName();
        String associatedName = adminRepository.findByPass(activity.getAssociated())
                .map(admin -> admin.getUser().getFullName())
                .orElse("");

        if(isLoggedInAdmin) {
            return switch (activity.getMode()) {
                case LOGIN -> "You logged in with password and One-Time Password";
                case MFA_LOGIN -> "You logged in with Multi-Factor authentication";
                case PASSWORD_CHANGE -> {
                    if(activity.getAssociated() != null) {
                        if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                            yield  "%s changed your password".formatted(name);
                        } else {
                            yield  "You changed password for %s".formatted(associatedName);
                        }
                    } else {
                        yield "You changed your password";
                    }
                }
                case SUPER_CREATE -> "Your super admin account has been created";
                case TEAM_ADD -> "You invited %s as a new team member".formatted(associatedName);
                case INVITE_ACCEPT -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s accepted your admin invite from".formatted(name);
                    } else {
                        yield "You accepted the Serch team admin invite from %s".formatted(associatedName);
                    }
                }
                default -> "";
            };
        } else {
            return switch (activity.getMode()) {
                case LOGIN -> "%s logged in with password and One-Time Password".formatted(name);
                case MFA_LOGIN -> "%s logged in with Multi-Factor authentication".formatted(name);
                case PASSWORD_CHANGE -> {
                    if(activity.getAssociated() != null) {
                        yield  "%s changed password for %s".formatted(name, associatedName);
                    } else {
                        yield "%s's password was changed".formatted(name);
                    }
                }
                case SUPER_CREATE -> "A super admin account for %s has been created".formatted(name);
                case TEAM_ADD -> "%s invited %s as a new team member".formatted(name, associatedName);
                case INVITE_ACCEPT -> "%s accepted the Serch team admin invite %s".formatted(name, associatedName);
                default -> "";
            };
        }
    }

    @Override
    @Transactional
    public void create(ActivityMode mode, String associated, String account, Admin admin) {
        AdminActivity activity = new AdminActivity();
        activity.setAdmin(admin);
        activity.setAssociated(associated);
        activity.setAccount(account);
        activity.setMode(mode);
        adminActivityRepository.save(activity);
    }

    @Override
    @Transactional
    public void create(ActivityMode mode, String associated, String account, User user) {
        Admin admin = adminRepository.findById(user.getId())
                .orElseThrow(() -> new AuthException("Admin not found"));
        create(mode, associated, account, admin);
    }
}