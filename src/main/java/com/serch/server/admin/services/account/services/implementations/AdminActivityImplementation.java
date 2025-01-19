package com.serch.server.admin.services.account.services.implementations;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.models.AdminActivity;
import com.serch.server.admin.repositories.AdminActivityRepository;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.admin.services.team.responses.AdminActivityResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public List<AdminActivityResponse> activities(UUID id, Integer page, Integer size) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin account = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));

        Page<AdminActivity> activities = adminActivityRepository.findByAdminId(id, account.getPass(), HelperUtil.getPageable(page, size));
        if(activities == null || activities.isEmpty() || !activities.hasContent()) {
            return new ArrayList<>();
        } else {
            return activities.getContent()
                    .stream()
                    .sorted(Comparator.comparing(AdminActivity::getCreatedAt).reversed()).map(activity -> response(
                            activity,
                            admin.getUser().getRole() != Role.TEAM,
                            admin.getId().equals(id)
                    )).toList();
        }
    }

    @Override
    @Transactional
    public List<AdminActivityResponse> activities(Integer page, Integer size) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        return switch (admin.getUser().getRole()) {
            case SUPER_ADMIN -> getAdminActivities(adminActivityRepository.findAll(HelperUtil.getPageable(page, size)), admin);
            case ADMIN -> getAdminActivities(adminActivityRepository.findAdmin(admin.getId(), admin.getPass(), HelperUtil.getPageable(page, size)), admin);
            case MANAGER -> getAdminActivities(adminActivityRepository.findManager(admin.getId(), admin.getPass(), HelperUtil.getPageable(page, size)), admin);
            default -> activities(admin.getId(), page, size);
        };
    }

    private List<AdminActivityResponse> getAdminActivities(Page<AdminActivity> activities, Admin admin) {
        if (activities == null || activities.isEmpty() || !activities.hasContent()) {
            return new ArrayList<>();
        } else {
            return activities.getContent()
                    .stream()
                    .sorted(Comparator.comparing(AdminActivity::getCreatedAt).reversed()).map(activity -> response(
                            activity,
                            admin.getUser().getRole() != Role.TEAM,
                            true
                    )).toList();
        }
    }

    private AdminActivityResponse response(AdminActivity activity, boolean showAssociated, boolean isLoggedInAdmin) {
        AdminActivityResponse response = AdminMapper.instance.response(activity);
        response.setLabel(TimeUtil.formatDay(activity.getUpdatedAt(), activity.getAdmin().getUser().getTimezone()));
        response.setActivity(activity(isLoggedInAdmin, activity));
        response.setTag(adminRepository.findByPass(activity.getAssociated())
                .map(admin -> admin.getUser().getFullName())
                .orElse(""));
        response.setAssociated(adminRepository.findByPass(activity.getAssociated())
                .map(admin -> admin.getUser().getId().toString())
                .orElse(""));

        if(!showAssociated) {
            response.setAssociated("");
            response.setTag("");
        }

        response.setHeader(activity.getMode().getValue());
        response.setCreatedAt(TimeUtil.toUserTimeZone(activity.getCreatedAt(), activity.getAdmin().getUser().getTimezone()));
        response.setUpdatedAt(TimeUtil.toUserTimeZone(activity.getUpdatedAt(), activity.getAdmin().getUser().getTimezone()));

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
                            yield "%s changed your password".formatted(name);
                        } else {
                            yield "You changed password for %s".formatted(associatedName);
                        }
                    } else {
                        yield "You changed your password";
                    }
                }
                case PASSWORD_CHANGE_REQUEST -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s sent you a reset password link".formatted(name);
                    } else {
                        yield "You sent a reset password link to %s".formatted(associatedName);
                    }
                }
                case STATUS_CHANGE -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s changed your status".formatted(name);
                    } else {
                        yield "You changed status for %s".formatted(associatedName);
                    }
                }
                case PROFILE_UPDATE -> {
                    if(activity.getAssociated() != null) {
                        if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                            yield "%s update your profile information".formatted(name);
                        } else {
                            yield "You updated %s's profile information".formatted(associatedName);
                        }
                    } else {
                        yield "You update your profile information";
                    }
                }
                case ROLE_CHANGE -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s changed your admin role".formatted(name);
                    } else {
                        yield "You changed admin role for %s".formatted(associatedName);
                    }
                }
                case MFA_CONSTRAINT_ENFORCED -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s has enforced multi-factor authentication on your account. Portal access is now granted after MFA".formatted(name);
                    } else {
                        yield "You enforced multi-factor authentication on %s's account. Portal access is now granted after MFA".formatted(associatedName);
                    }
                }
                case MFA_CONSTRAINT_REMOVED -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s has removed forced multi-factor authentication on your account. Portal access is now granted after password login".formatted(name);
                    } else {
                        yield "You removed forced multi-factor authentication on %s's account. Portal access is now granted after password login".formatted(associatedName);
                    }
                }
                case AVATAR_CHANGE -> {
                    if(activity.getAssociated() != null) {
                        if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                            yield "%s changed your profile picture".formatted(name);
                        } else {
                            yield "You changed profile picture for %s".formatted(associatedName);
                        }
                    } else {
                        yield "You changed your profile picture";
                    }
                }
                case SUPER_CREATE -> "Your super admin account has been created";
                case TEAM_ADD -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s invited you as a new team member".formatted(name);
                    } else {
                        yield "You invited %s as a new team member".formatted(associatedName);
                    }
                }
                case INVITE_ACCEPT -> {
                    if(loggedInAdmin.getPass().equals(activity.getAssociated())) {
                        yield "%s accepted your admin invite".formatted(name);
                    } else {
                        yield "You accepted the admin invite from %s".formatted(associatedName);
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
                case PASSWORD_CHANGE_REQUEST -> {
                    if(activity.getAssociated() != null) {
                        yield "%s sent a reset password link to %s".formatted(name, associatedName);
                    } else {
                        yield "A reset password link was sent to %s".formatted(name);
                    }
                }
                case PROFILE_UPDATE -> {
                    if(activity.getAssociated() != null) {
                        yield "%s updated %s's your profile information".formatted(name, associatedName);
                    } else {
                        yield "%s's profile information was updated".formatted(name);
                    }
                }
                case AVATAR_CHANGE -> {
                    if(activity.getAssociated() != null) {
                        yield "%s changed %s's profile picture".formatted(name, associatedName);
                    } else {
                        yield "%s's profile picture was changed".formatted(name);
                    }
                }
                case STATUS_CHANGE -> {
                    if(activity.getAssociated() != null) {
                        yield  "%s changed status for %s".formatted(name, associatedName);
                    } else {
                        yield "%s's status was changed".formatted(name);
                    }
                }
                case ROLE_CHANGE -> {
                    if(activity.getAssociated() != null) {
                        yield  "%s changed admin role for %s".formatted(name, associatedName);
                    } else {
                        yield "%s's admin role was changed".formatted(name);
                    }
                }
                case MFA_CONSTRAINT_ENFORCED -> {
                    if(activity.getAssociated() != null) {
                        yield "%s enforced multi-factor authentication on %s account. Portal access is now granted after MFA".formatted(name, associatedName);
                    } else {
                        yield "Multi-factor authentication was enforced on %s's account. Portal access is now granted after MFA".formatted(name);
                    }
                }
                case MFA_CONSTRAINT_REMOVED -> {
                    if(activity.getAssociated() != null) {
                        yield "%s has removed forced multi-factor authentication on %s account. Portal access is now granted after password login".formatted(name, associatedName);
                    } else {
                        yield "Forced multi-factor authentication was removed on %s's account. Portal access is now granted after password login".formatted(name);
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