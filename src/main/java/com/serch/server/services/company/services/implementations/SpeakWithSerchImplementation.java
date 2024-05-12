package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.exceptions.others.CompanyException;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.company.Issue;
import com.serch.server.models.company.SpeakWithSerch;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.company.IssueRepository;
import com.serch.server.repositories.company.SpeakWithSerchRepository;
import com.serch.server.services.company.requests.IssueRequest;
import com.serch.server.services.company.services.SpeakWithSerchService;
import com.serch.server.services.company.responses.IssueResponse;
import com.serch.server.services.company.responses.SpeakWithSerchResponse;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpeakWithSerchImplementation implements SpeakWithSerchService {
    private final UserRepository userRepository;
    private final SpeakWithSerchRepository speakWithSerchRepository;
    private final IssueRepository issueRepository;

    @Override
    public ApiResponse<SpeakWithSerchResponse> lodgeIssue(IssueRequest request) {
        User user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new CompanyException("User not found"));

        Optional<SpeakWithSerch> existing = speakWithSerchRepository.findById(request.getTicket());
        if(existing.isPresent()) {
            saveNewIssue(request, user, existing.get());
            existing.get().setUpdatedAt(LocalDateTime.now());
            speakWithSerchRepository.save(existing.get());
            return new ApiResponse<>(prepareSpeakWithSerchResponse(existing.get()));
        } else {
            SpeakWithSerch serch = new SpeakWithSerch();
            serch.setUser(user);
            serch.setStatus(IssueStatus.OPENED);
            SpeakWithSerch saved = speakWithSerchRepository.save(serch);
            saved.setIssue(List.of(saveNewIssue(request, user, saved)));
            return new ApiResponse<>(prepareSpeakWithSerchResponse(saved));
        }
    }

    private Issue saveNewIssue(IssueRequest request, User user, SpeakWithSerch serch) {
        Issue issue = new Issue();
        issue.setComment(request.getComment());
        issue.setSender(String.valueOf(user.getId()));
        issue.setSpeakWithSerch(serch);
        issue.setIsRead(true);
        return issueRepository.save(issue);
    }

    private SpeakWithSerchResponse prepareSpeakWithSerchResponse(SpeakWithSerch speakWithSerch) {
        SpeakWithSerchResponse response = CompanyMapper.INSTANCE.response(speakWithSerch);
        response.setLabel(TimeUtil.formatDay(speakWithSerch.getCreatedAt()));
        response.setTime(TimeUtil.formatDay(speakWithSerch.getUpdatedAt()));
        response.setIssues(
                speakWithSerch.getIssue() != null
                        ? speakWithSerch.getIssue().stream()
                        .sorted(Comparator.comparing(Issue::getCreatedAt))
                        .map(issue -> {
                            IssueResponse res = CompanyMapper.INSTANCE.response(issue);
                            res.setIsSerch(!issue.getSender().equals(String.valueOf(speakWithSerch.getUser().getId())));
                            res.setLabel(TimeUtil.formatDay(issue.getCreatedAt()));
                            return res;
                        }).toList()
                        : List.of()
        );
        return response;
    }

    @Override
    public ApiResponse<List<SpeakWithSerchResponse>> message() {
        User user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new CompanyException("User not found"));

        List<SpeakWithSerch> speak = speakWithSerchRepository.findByUser_Id(user.getId());
        if(speak != null) {
            return new ApiResponse<>(
                    speak.stream()
                            .sorted(Comparator.comparing(SpeakWithSerch::getUpdatedAt))
                            .map(this::prepareSpeakWithSerchResponse).toList()
            );
        } else {
            return new ApiResponse<>(List.of());
        }
    }

    @Override
    public ApiResponse<List<SpeakWithSerchResponse>> markRead(String ticket) {
        userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new CompanyException("User not found"));
        SpeakWithSerch speakWithSerch = speakWithSerchRepository.findById(ticket)
                .orElseThrow(() -> new CompanyException("Content not found. Might have been removed by Serch"));
        speakWithSerch.getIssue().stream()
                .filter(serch -> !serch.getIsRead())
                .forEach(serch -> {
                    serch.setIsRead(true);
                    serch.setUpdatedAt(LocalDateTime.now());
                    issueRepository.save(serch);
                });
        return message();
    }

    @Override
    public void removeOldContents() {
        LocalDateTime date = LocalDateTime.now().minusYears(5);
        List<SpeakWithSerch> list = speakWithSerchRepository.findByCreatedAtBefore(date);
        if(list != null && !list.isEmpty()) {
            speakWithSerchRepository.deleteAll(list);
        }
    }
}
