package com.serch.server.admin.services.account.services;

import com.serch.server.admin.exceptions.AdminException;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.account.responses.*;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.AccountStatus.*;
import static com.serch.server.enums.auth.Role.*;

@Service
@RequiredArgsConstructor
public class TeamImplementation implements TeamService {
    private final AdminActivityService activityService;
    private final AdminProfileService adminProfileService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public ApiResponse<TeamOverviewResponse> overview() {
        TeamOverviewResponse response = new TeamOverviewResponse();
        response.setOverview(getOverview());
        response.setTeams(getTeams());
        response.setStructure(team());
        response.setActivities(activityService.activities());
        return new ApiResponse<>(response);
    }

    private List<Metric> getOverview() {
        return Arrays.stream(AccountStatus.values())
                .filter(status -> status == ACTIVE || status == SUSPENDED || status == DELETED)
                .map(status -> {
                    Metric metric = new Metric();
                    metric.setCount(String.valueOf(userRepository.countAdminByStatus(status)));
                    metric.setHeader(status.getType());
                    metric.setFeature(status.getType());
                    return metric;
                })
                .toList();
    }

    private List<Metric> getTeams() {
        return Arrays.stream(Role.values())
                .filter(role -> role == ADMIN || role == SUPER_ADMIN || role == MANAGER || role == TEAM)
                .map(role -> {
                    Metric metric = new Metric();
                    metric.setCount(String.valueOf(userRepository.countByRole(role)));
                    metric.setHeader(role.getType());
                    return metric;
                })
                .toList();
    }

    @Override
    @Transactional
    public CompanyStructure team() {
        Admin superAdmin = adminRepository.findByUser_Role(SUPER_ADMIN)
                .orElseThrow(() -> new AdminException("Super admin not found"));

        CompanyStructure superAdminNode = createCompanyStructure(superAdmin);
        List<Admin> admins = adminRepository.findAll()
                .stream().filter(admin -> admin.getUser().getRole() != SUPER_ADMIN)
                .toList();

        Map<UUID, List<Admin>> adminsByAddedBy = admins.stream().collect(Collectors.groupingBy(admin ->
                admin.getAdmin() != null ? admin.getAdmin().getId() : superAdmin.getId()));
        addChildren(superAdminNode, superAdmin.getId(), adminsByAddedBy);

        return superAdminNode;
    }

    private void addChildren(CompanyStructure parentNode, UUID parentId, Map<UUID, List<Admin>> adminsByAddedBy) {
        List<Admin> children = adminsByAddedBy.get(parentId);
        if (children != null && !children.isEmpty()) {
            for (Admin child : children) {
                CompanyStructure childNode = createCompanyStructure(child);
                parentNode.addChild(childNode);
                addChildren(childNode, child.getId(), adminsByAddedBy);
            }
        }
    }

    private CompanyStructure createCompanyStructure(Admin admin) {
        CompanyStructure structure = new CompanyStructure();
        structure.setId(String.valueOf(admin.getId()));
        structure.setName(admin.getUser().getFullName());
        structure.setPosition(admin.getPosition());
        structure.setRole(admin.getUser().getRole());
        structure.setImage(admin.getAvatar());
        return structure;
    }

    @Override
    @Transactional
    public ApiResponse<AdminListResponse> admins() {
        AdminListResponse response = new AdminListResponse();
        response.setAdmins(groups());
        response.setStructure(team());
        return new ApiResponse<>(response);
    }

    private List<Admin> getAdmins() {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AdminException("Admin not found"));
        List<Admin> admins;
        if(admin.isSuper()) {
            admins = adminRepository.findAll();
        } else {
            admins = admin.getAdmins().stream().toList();
        }
        return admins;
    }

    private List<AdminGroupResponse> groups() {
        List<Admin> admins = getAdmins();
        if(admins != null && !admins.isEmpty()) {
            Map<Role, List<Admin>> groups = admins.stream().collect(Collectors.groupingBy(admin -> admin.getUser().getRole()));
            List<AdminGroupResponse> response = new ArrayList<>();
            groups.forEach((role, list) -> {
                AdminGroupResponse group = new AdminGroupResponse();
                group.setRole(role.getType());
                group.setAdmins(list.stream().map(adminProfileService::profile).toList());
                response.add(group);
            });
            return response;
        } else {
            return new ArrayList<>();
        }
    }
}
