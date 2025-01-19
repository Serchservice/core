package com.serch.server.admin.mappers;

import com.serch.server.admin.models.*;
import com.serch.server.admin.models.permission.GrantedPermissionScope;
import com.serch.server.admin.models.permission.RequestedPermission;
import com.serch.server.admin.services.team.responses.AdminActivityResponse;
import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import com.serch.server.admin.services.notification.responses.AdminNotificationResponse;
import com.serch.server.admin.services.organization.data.OrganizationDto;
import com.serch.server.admin.services.organization.data.OrganizationResponse;
import com.serch.server.admin.services.permission.responses.PermissionRequestResponse;
import com.serch.server.admin.services.permission.responses.GrantedPermissionScopeResponse;
import com.serch.server.admin.services.team.responses.CompanyStructure;
import com.serch.server.models.auth.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminMapper {
    AdminMapper instance = Mappers.getMapper(AdminMapper.class);

    AdminActivityResponse response(AdminActivity activity);

    AdminNotificationResponse response(AdminNotification adminNotification);

    @Mappings({
            @Mapping(target = "createdAt", source = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", source = "updatedAt", ignore = true),
            @Mapping(target = "permissions", source = "permissions", ignore = true),
    })
    GrantedPermissionScopeResponse response(GrantedPermissionScope permission);

    @Mapping(target = "account", source = "account", ignore = true)
    PermissionRequestResponse response(RequestedPermission permission);

    AdminProfileResponse response(User user);

    OrganizationResponse response(Organization organization);

    Organization toEntity(OrganizationDto organizationDto);

    OrganizationDto toDto(Organization organization);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Organization partialUpdate(OrganizationDto organizationDto, @MappingTarget Organization organization);

    @Mappings({
            @Mapping(target = "name", source = "user.fullName"),
            @Mapping(target = "role", source = "user.role"),
            @Mapping(target = "image", source = "avatar"),
            @Mapping(target = "id", source = "user.id"),
    })
    CompanyStructure structure(Admin admin);
}