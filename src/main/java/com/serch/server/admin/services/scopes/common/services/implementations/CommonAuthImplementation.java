package com.serch.server.admin.services.scopes.common.services.implementations;

import com.serch.server.admin.mappers.AnalysisMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.responses.auth.*;
import com.serch.server.admin.services.scopes.common.services.CommonAuthService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthLevel;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.auth.Session;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAChallenge;
import com.serch.server.repositories.auth.RefreshTokenRepository;
import com.serch.server.repositories.auth.SessionRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.mfa.MFAChallengeRepository;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonAuthImplementation implements CommonAuthService {
    private final AdminRepository adminRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MFAChallengeRepository mFAChallengeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AccountMFAResponse mfa(User user) {
        if(user.getMfaFactor() == null) {
            return new AccountMFAResponse();
        } else {
            return AnalysisMapper.instance.mfa(user.getMfaFactor());
        }
    }

    @Override
    @Transactional
    public List<AccountMFAChallengeResponse> challenges(UUID id, Integer page, Integer size) {
        Page<MFAChallenge> challenges = mFAChallengeRepository.findByMfaFactor_User_Id(id, HelperUtil.getPageable(page, size));
        if(challenges == null || challenges.isEmpty() || !challenges.hasContent()) {
            return new ArrayList<>();
        }

        return challenges.getContent()
                .stream()
                .sorted(Comparator.comparing(MFAChallenge::getCreatedAt).reversed())
                .map(AnalysisMapper.instance::challenge)
                .toList();
    }

    @Override
    @Transactional
    public List<AccountSessionResponse> sessions(UUID id, Integer page, Integer size) {
        Page<Session> sessions = sessionRepository.getByUser_Id(id, HelperUtil.getPageable(page, size));
        if(sessions == null || sessions.isEmpty() || !sessions.hasContent()) {
            return new ArrayList<>();
        }

        return sessions.getContent()
                .stream()
                .sorted(Comparator.comparing(Session::getCreatedAt).reversed())
                .map(AnalysisMapper.instance::session)
                .toList();
    }

    @Override
    public List<AccountAuthDeviceResponse> devices(UUID id, Integer page, Integer size) {
        Page<Session> sessions = sessionRepository.getByUser_Id(id, HelperUtil.getPageable(page, size));
        if(sessions == null || sessions.isEmpty() || !sessions.hasContent()) {
            return new ArrayList<>();
        }

        Map<String, List<Session>> groups = sessions.getContent()
                .stream()
                .collect(Collectors.groupingBy(Session::getIpAddress));

        List<AccountAuthDeviceResponse> devices = new ArrayList<>();
        groups.forEach((k, v) -> {
            v.sort(Comparator.comparing(Session::getCreatedAt).reversed());
            Session latest = v.getFirst();

            AccountAuthDeviceResponse device = new AccountAuthDeviceResponse();
            device.setCount(v.size());
            device.setName(k);
            device.setRevoked(latest.getRevoked());

            if(latest.getName() == null || latest.getName().isEmpty()) {
                device.setPlatform(String.format("Unknown | %s", latest.getPlatform()));
            } else {
                device.setPlatform(String.format("%s | %s", latest.getName(), latest.getPlatform()));
            }

            devices.add(device);
        });

        return devices;
    }

    @Override
    @Transactional
    public <T extends AccountAuthResponse> T auth(UUID id, Integer page, Integer size, T response) {
        User user = userRepository.findById(id).orElseThrow(() -> new SerchException("User not found"));

        response.setHasMFA(user.hasMFA());
        response.setDevices(devices(id, page, size));
        response.setMustHaveMFA(adminRepository.findById(user.getId()).map(Admin::getMustHaveMFA).orElse(false));

        List<Session> list = sessionRepository.findMostRecentSessionByUser(user.getId(), PageRequest.of(0, 1));
        if(list == null || list.isEmpty()) {
            response.setMethod(String.format("%s - %s", AuthMethod.NONE, AuthMethod.NONE.getType()));
            response.setLevel(String.format("%s - %s", AuthLevel.LEVEL_0, AuthLevel.LEVEL_0.getType()));
        } else {
            Session recent = list.getFirst();
            response.setMethod(String.format("%s - %s", recent.getMethod(), recent.getMethod().getType()));
            response.setLevel(String.format("%s - %s", recent.getAuthLevel(), recent.getAuthLevel().getType()));
        }

        return response;
    }

    @Override
    @Transactional
    public ApiResponse<List<AccountSessionResponse>> revokeSession(UUID sessionId) {
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SerchException("Invalid session id"));

        if(session.getRevoked()) {
            throw new SerchException("Session is already revoked");
        } else {
            session.setRevoked(true);
            session.setUpdatedAt(TimeUtil.now());
            sessionRepository.save(session);

            return new ApiResponse<>("Session is now revoked", sessions(session.getUser().getId(), null, null), HttpStatus.OK);
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<AccountSessionResponse>> revokeRefreshToken(UUID refreshTokenId) {
        var refreshToken = refreshTokenRepository.findById(refreshTokenId)
                .orElseThrow(() -> new SerchException("Invalid refresh token id"));

        if(refreshToken.getRevoked()) {
            throw new SerchException("Refresh Token is already revoked");
        } else {
            refreshToken.setRevoked(true);
            refreshToken.setUpdatedAt(TimeUtil.now());
            refreshTokenRepository.save(refreshToken);

            return new ApiResponse<>("Refresh Token is now revoked", sessions(refreshToken.getUser().getId(), null, null), HttpStatus.OK);
        }
    }
}