package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.exceptions.PermissionException;
import com.serch.server.admin.mappers.AdminCompanyMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.scopes.common.CommonProfileService;
import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchOverviewResponse;
import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchScopeResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.bases.BaseProfile;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.models.company.Issue;
import com.serch.server.models.company.SpeakWithSerch;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.company.IssueRepository;
import com.serch.server.repositories.company.SpeakWithSerchRepository;
import com.serch.server.services.company.requests.IssueRequest;
import com.serch.server.services.company.responses.IssueResponse;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpeakWithSerchScopeImplementation implements SpeakWithSerchScopeService {
    private final CommonProfileService profileService;
    private final SpeakWithSerchRepository speakWithSerchRepository;
    private final AdminRepository adminRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    @Transactional
    public ApiResponse<SpeakWithSerchOverviewResponse> overview() {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        SpeakWithSerchOverviewResponse response = new SpeakWithSerchOverviewResponse();
        List<SpeakWithSerch> assigned = speakWithSerchRepository.findAssigned(admin.getId());
        if(assigned != null && !assigned.isEmpty()) {
            List<SpeakWithSerchScopeResponse> assignedList = assigned.stream()
                    .sorted(Comparator.comparing(SpeakWithSerch::getUpdatedAt).reversed())
                    .map(this::response).toList();
            response.setAssigned(assignedList);
        }

        List<SpeakWithSerch> others = speakWithSerchRepository.findOthers(admin.getId());
        if(!others.isEmpty()) {
            List<SpeakWithSerchScopeResponse> otherList = others.stream()
                    .sorted(Comparator.comparing(SpeakWithSerch::getUpdatedAt).reversed())
                    .map(this::response).toList();
            response.setOthers(otherList);
        }
        return new ApiResponse<>(response);
    }

    private boolean canReply(SpeakWithSerch speakWithSerch) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        return admin.isSuper() || admin.isAdmin() || speakWithSerch.getAssignedAdmin() == null
                || speakWithSerch.getAssignedAdmin().getUser().isUser(admin.getId());
    }

    private SpeakWithSerchScopeResponse response(SpeakWithSerch speakWithSerch) {
        SpeakWithSerchScopeResponse response = AdminCompanyMapper.instance.response(speakWithSerch);
        response.setLabel(TimeUtil.formatDay(speakWithSerch.getCreatedAt(), ""));
        response.setTime(TimeUtil.formatDay(speakWithSerch.getUpdatedAt(), ""));
        if(response.getAssignedAdmin() != null) {
            response.getAssignedAdmin().setFirstName(speakWithSerch.getAssignedAdmin().getUser().getFirstName());
            response.getAssignedAdmin().setLastName(speakWithSerch.getAssignedAdmin().getUser().getLastName());
            response.getAssignedAdmin().setRole(speakWithSerch.getAssignedAdmin().getUser().getRole());
        }
        if(response.getResolvedBy() != null) {
            response.getResolvedBy().setFirstName(speakWithSerch.getResolvedBy().getUser().getFirstName());
            response.getResolvedBy().setLastName(speakWithSerch.getResolvedBy().getUser().getLastName());
            response.getResolvedBy().setRole(speakWithSerch.getResolvedBy().getUser().getRole());
        }
        if(response.getClosedBy() != null) {
            response.getClosedBy().setFirstName(speakWithSerch.getClosedBy().getUser().getFirstName());
            response.getClosedBy().setLastName(speakWithSerch.getClosedBy().getUser().getLastName());
            response.getClosedBy().setRole(speakWithSerch.getClosedBy().getUser().getRole());
        }
        response.getUser().setAvatar(profileRepository.findById(speakWithSerch.getUser().getId())
                .map(BaseProfile::getAvatar)
                .orElse(businessProfileRepository.findById(speakWithSerch.getUser().getId())
                        .map(BaseProfile::getAvatar)
                        .orElse("")
                ));
        response.setIssues(
                speakWithSerch.getIssues() != null
                        ? speakWithSerch.getIssues().stream()
                        .sorted(Comparator.comparing(Issue::getCreatedAt))
                        .map(issue -> {
                            IssueResponse res = CompanyMapper.INSTANCE.response(issue);
                            res.setIsSerch(!issue.getSender().equals(String.valueOf(speakWithSerch.getUser().getId())));
                            res.setLabel(TimeUtil.formatDay(issue.getCreatedAt(), ""));

                            userRepository.findById(UUID.fromString(issue.getSender()))
                                    .ifPresent(user -> res.setProfile(profileService.fromUser(user)));
                            return res;
                        }).toList()
                        : List.of()
        );
        return response;
    }

    @Override
    @Transactional
    public ApiResponse<SpeakWithSerchScopeResponse> reply(IssueRequest request) {
        SpeakWithSerch speakWithSerch = speakWithSerchRepository.findById(request.getTicket())
                .orElseThrow(() -> new SerchException("Ticket not found"));
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(canReply(speakWithSerch)) {
            Issue issue = new Issue();
            issue.setSender(String.valueOf(admin.getId()));
            issue.setComment(request.getComment());
            issue.setSpeakWithSerch(speakWithSerch);
            issueRepository.save(issue);

            speakWithSerch.setStatus(IssueStatus.WAITING);
            speakWithSerchRepository.save(speakWithSerch);

            if(speakWithSerch.getAssignedAdmin() == null) {
                speakWithSerch.setAssignedAdmin(admin);
                speakWithSerch.setUpdatedAt(TimeUtil.now());
                speakWithSerchRepository.save(speakWithSerch);
                speakWithSerch.getIssues().add(issue);
            }
            return new ApiResponse<>(response(speakWithSerch));
        } else {
            throw new PermissionException("Access denied. You can only reply to tickets you have been assigned to");
        }
    }

    @Override
    @Transactional
    public ApiResponse<SpeakWithSerchScopeResponse> find(String ticket) {
        SpeakWithSerch speakWithSerch = speakWithSerchRepository.findById(ticket)
                .orElseThrow(() -> new SerchException("Ticket not found"));
        if(canReply(speakWithSerch)) {
            return new ApiResponse<>(response(speakWithSerch));
        } else {
            throw new PermissionException("Access denied. You can only resolve tickets you have been assigned to");
        }
    }

    private boolean canAct(SpeakWithSerch speakWithSerch) {
        return speakWithSerch.getIssues() != null && speakWithSerch.getIssues()
                .stream()
                .anyMatch(issue -> !issue.getSender().equals(String.valueOf(speakWithSerch.getUser().getId())));
    }

    @Override
    @Transactional
    public ApiResponse<SpeakWithSerchScopeResponse> resolve(String ticket) {
        SpeakWithSerch speakWithSerch = speakWithSerchRepository.findById(ticket)
                .orElseThrow(() -> new SerchException("Ticket not found"));
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(canReply(speakWithSerch)) {
            if(canAct(speakWithSerch)) {
                speakWithSerch.setStatus(IssueStatus.RESOLVED);
                speakWithSerch.setUpdatedAt(TimeUtil.now());
                speakWithSerch.setResolvedBy(admin);
                speakWithSerchRepository.save(speakWithSerch);
                return new ApiResponse<>("Ticket is now resolved", response(speakWithSerch), HttpStatus.OK);
            } else {
                throw new PermissionException("Access denied. Ticket cannot be resolved without any reply from Serch");
            }
        } else {
            throw new PermissionException("Access denied. You can only resolve tickets you have been assigned to");
        }
    }

    @Override
    @Transactional
    public ApiResponse<SpeakWithSerchScopeResponse> assign(String ticket, UUID id) {
        SpeakWithSerch speakWithSerch = speakWithSerchRepository.findById(ticket)
                .orElseThrow(() -> new SerchException("Ticket not found"));
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));
        speakWithSerch.setAssignedAdmin(admin);
        speakWithSerch.setUpdatedAt(TimeUtil.now());
        speakWithSerchRepository.save(speakWithSerch);
        return new ApiResponse<>(response(speakWithSerch));
    }

    @Override
    @Transactional
    public ApiResponse<SpeakWithSerchScopeResponse> close(String ticket) {
        SpeakWithSerch speakWithSerch = speakWithSerchRepository.findById(ticket)
                .orElseThrow(() -> new SerchException("Ticket not found"));
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        if(canReply(speakWithSerch)) {
            if(canAct(speakWithSerch)) {
                speakWithSerch.setStatus(IssueStatus.CLOSED);
                speakWithSerch.setUpdatedAt(TimeUtil.now());
                speakWithSerch.setClosedBy(admin);
                speakWithSerchRepository.save(speakWithSerch);
                return new ApiResponse<>("Ticket is now closed", response(speakWithSerch), HttpStatus.OK);
            } else {
                throw new PermissionException("Access denied. Ticket cannot be closed without any reply from Serch");
            }
        } else {
            throw new PermissionException("Access denied. You can only close tickets you have been assigned to");
        }
    }
}
