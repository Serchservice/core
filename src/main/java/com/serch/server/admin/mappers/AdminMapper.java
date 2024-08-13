package com.serch.server.admin.mappers;

import com.serch.server.admin.models.*;
import com.serch.server.admin.services.account.responses.AdminActivityResponse;
import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import com.serch.server.admin.services.notification.AdminNotificationResponse;
import com.serch.server.admin.services.permission.responses.PermissionRequestResponse;
import com.serch.server.admin.services.permission.responses.PermissionScopeResponse;
import com.serch.server.models.auth.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapper {
    AdminMapper instance = Mappers.getMapper(AdminMapper.class);

    AdminActivityResponse response(AdminActivity activity);

    AdminNotificationResponse response(AdminNotification adminNotification);

    @Mappings({
            @Mapping(target = "createdAt", source = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", source = "updatedAt", ignore = true),
            @Mapping(target = "permissions", source = "permissions", ignore = true),
    })
    PermissionScopeResponse response(GrantedPermissionScope permission);

    PermissionRequestResponse response(RequestedPermission permission);

    AdminProfileResponse response(User user);
}