package com.serch.server.domains.nearby.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.services.FileService;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.repositories.go.GoUserRepository;
import com.serch.server.domains.nearby.services.account.services.GoLocationService;
import com.serch.server.domains.nearby.services.account.requests.GoAccountUpdateRequest;
import com.serch.server.domains.nearby.services.account.responses.GoAccountResponse;
import com.serch.server.domains.nearby.services.account.services.GoAccountService;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoAccountImplementation implements GoAccountService {
    private final AuthUtil authUtil;
    private final FileService fileService;
    private final GoLocationService locationService;
    private final GoUserRepository goUserRepository;

    @Override
    public ApiResponse<GoAccountResponse> update(GoAccountUpdateRequest request) {
        GoUser user = authUtil.getGoUser();

        String image = null;
        if(request.getUpload() != null) {
            image = fileService.uploadGo(request.getUpload(), user.getId()).getFile();
        }

        GoMapper.instance.update(request, user);
        locationService.put(user, request.getAddress());

        user = goUserRepository.save(user);

        if(image != null) {
            user.setAvatar(image);
        }

        return new ApiResponse<>(getGoAccountResponse(user));
    }

    private GoAccountResponse getGoAccountResponse(GoUser user) {
        GoAccountResponse response = GoMapper.instance.account(user);
        response.setJoinedOn(TimeUtil.getLabel(user.getCreatedAt(), user.getTimezone()));
        response.setEmailConfirmedAt(TimeUtil.formatDay(user.getEmailConfirmedAt(), user.getTimezone()));

        return response;
    }

    @Override
    public ApiResponse<GoAccountResponse> get() {
        return new ApiResponse<>(getGoAccountResponse(authUtil.getGoUser()));
    }
}