package com.serch.server.admin.services.scopes.common;

import com.serch.server.admin.mappers.AnalysisMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.responses.auth.*;
import com.serch.server.enums.auth.AuthLevel;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.models.auth.Session;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAChallenge;
import com.serch.server.repositories.auth.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonAuthImplementation implements CommonAuthService {
    private final AdminRepository adminRepository;
    private final SessionRepository sessionRepository;

    @Override
    public AccountMFAResponse mfa(User user) {
        return AnalysisMapper.instance.mfa(user.getMfaFactor());
    }

    @Override
    public List<AccountMFAChallengeResponse> challenges(User user) {
        List<MFAChallenge> challenges = user.getMfaFactor().getMfaChallenges();
        if(challenges == null || challenges.isEmpty()) {
            return new ArrayList<>();
        }

        return challenges.stream().sorted(Comparator.comparing(MFAChallenge::getCreatedAt).reversed())
                .map(AnalysisMapper.instance::challenge).toList();
    }

    @Override
    public List<AccountSessionResponse> sessions(User user) {
        List<Session> sessions = user.getSessions();
        if(sessions == null || sessions.isEmpty()) {
            return new ArrayList<>();
        }

        return sessions.stream().sorted(Comparator.comparing(Session::getCreatedAt).reversed())
                .map(AnalysisMapper.instance::session).toList();
    }

    @Override
    public AccountAuthResponse auth(User user) {
        AccountAuthResponse response = new AccountAuthResponse();
        response.setHasMFA(user.hasMFA());
        response.setMustHaveMFA(adminRepository.findById(user.getId()).map(Admin::getMustHaveMFA).orElse(false));

        List<Session> list = sessionRepository.findMostRecentSessionByUser(user.getId(), PageRequest.of(0, 1));
        if(list == null || list.isEmpty()) {
            response.setMethod(String.format("%s - %s", AuthMethod.NONE, AuthMethod.NONE.getType()));
            response.setLevel(String.format("%s - %s", AuthLevel.LEVEL_0, AuthLevel.LEVEL_0.getType()));
        } else {
            Session recent = list.get(0);
            response.setMethod(String.format("%s - %s", recent.getMethod(), recent.getMethod().getType()));
            response.setLevel(String.format("%s - %s", recent.getAuthLevel(), recent.getAuthLevel().getType()));
        }

        if(user.getSessions() == null || user.getSessions().isEmpty()) {
            response.setDevices(new ArrayList<>());
        } else {
            Map<String, List<Session>> groups = sessionRepository.findByUser_Id(user.getId())
                            .stream().collect(Collectors.groupingBy(Session::getName));
            List<AccountAuthDeviceResponse> devices = new ArrayList<>();
            groups.forEach((k, v) -> {
                v.sort(Comparator.comparing(Session::getCreatedAt).reversed());
                Session latestSession = v.get(0);
                AccountAuthDeviceResponse device = new AccountAuthDeviceResponse();
                device.setCount(v.size());
                device.setName(k);
                device.setPlatform(latestSession.getPlatform());
                device.setRevoked(latestSession.getRevoked());
                devices.add(device);
            });
            response.setDevices(devices);
        }
        return response;
    }
}