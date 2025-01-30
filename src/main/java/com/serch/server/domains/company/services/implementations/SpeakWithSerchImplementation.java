package com.serch.server.domains.company.services.implementations;

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
import com.serch.server.domains.company.requests.IssueRequest;
import com.serch.server.domains.company.responses.IssueResponse;
import com.serch.server.domains.company.responses.SpeakWithSerchResponse;
import com.serch.server.domains.company.services.SpeakWithSerchService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpeakWithSerchImplementation implements SpeakWithSerchService {
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final SpeakWithSerchRepository speakWithSerchRepository;
    private final IssueRepository issueRepository;

    @Override
    public ApiResponse<SpeakWithSerchResponse> lodgeIssue(IssueRequest request) {
        User user = userRepository.findByEmailAddressIgnoreCase(AuthUtil.getAuth())
                .orElseThrow(() -> new CompanyException("User not found"));

        Optional<SpeakWithSerch> existing = speakWithSerchRepository.findById(request.getTicket());
        if(existing.isPresent()) {
            if(existing.get().getStatus() == IssueStatus.CLOSED) {
                throw new CompanyException("This ticket has been closed. Please open a new one");
            } else if(existing.get().getStatus() == IssueStatus.RESOLVED) {
                throw new CompanyException("This ticket has been marked as resolved. Please open a new one");
            } else {
                saveNewIssue(request, user, existing.get());
                existing.get().setUpdatedAt(TimeUtil.now());
                speakWithSerchRepository.save(existing.get());

                return new ApiResponse<>(prepareSpeakWithSerchResponse(existing.get()));
            }
        } else {
            SpeakWithSerch saved = getSpeakWithSerch(user);
            saved.setIssues(List.of(saveNewIssue(request, user, saved)));

            return new ApiResponse<>(prepareSpeakWithSerchResponse(saved));
        }
    }

    private SpeakWithSerch getSpeakWithSerch(User user) {
        SpeakWithSerch serch = new SpeakWithSerch();
        serch.setUser(user);
        serch.setStatus(IssueStatus.OPENED);

        return speakWithSerchRepository.save(serch);
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
        response.setLabel(TimeUtil.formatDay(speakWithSerch.getCreatedAt(), ""));
        response.setTime(TimeUtil.formatDay(speakWithSerch.getUpdatedAt(), ""));
        response.setTotal(speakWithSerch.getIssues().size());
        response.setHasSerchMessage(speakWithSerch.getIssues().stream().anyMatch((i) -> !i.getIsRead() && !i.getSender().equals(authUtil.getUser().getId().toString())));

        if(!speakWithSerch.getIssues().isEmpty()) {
            response.setIssues(getIssues(speakWithSerch.getTicket(), null,  null));
        }

        return response;
    }

    @Override
    public ApiResponse<List<SpeakWithSerchResponse>> messages(Integer page, Integer size) {
        return new ApiResponse<>(getTickets( page, size));
    }

    private List<SpeakWithSerchResponse> getTickets( Integer page, Integer size) {
        Page<SpeakWithSerch> speak = speakWithSerchRepository
                .findByUserId(authUtil.getUser().getId(), HelperUtil.getPageable(page, size));

        if(speak != null) {
            return speak.getContent()
                    .stream()
                    .sorted(Comparator.comparing(SpeakWithSerch::getUpdatedAt))
                    .map(this::prepareSpeakWithSerchResponse)
                    .toList();
        } else {
            return List.of();
        }
    }

    @Override
    public ApiResponse<List<IssueResponse>> issues(String ticket, Integer page, Integer size) {
        return new ApiResponse<>(getIssues(ticket, page, size));
    }

    private List<IssueResponse> getIssues(String ticket, Integer page, Integer size) {
        Page<Issue> issues = issueRepository.findBySpeakWithSerchTicket(ticket, HelperUtil.getPageable(page, size));

        if(issues != null) {
            return issues.getContent()
                    .stream()
                    .sorted(Comparator.comparing(Issue::getUpdatedAt))
                    .map(this::getIssueResponse)
                    .toList();
        } else {
            return List.of();
        }
    }

    private IssueResponse getIssueResponse(Issue issue) {
        IssueResponse response = CompanyMapper.INSTANCE.response(issue);
        response.setIsSerch(!issue.getSender().equals(String.valueOf(authUtil.getUser().getId())));
        response.setLabel(TimeUtil.formatDay(issue.getCreatedAt(), ""));

        return response;
    }

    @Override
    public ApiResponse<List<SpeakWithSerchResponse>> markRead(String ticket) {
        SpeakWithSerch speakWithSerch = speakWithSerchRepository.findById(ticket)
                .orElseThrow(() -> new CompanyException("Content not found. Might have been removed by Serch"));

        speakWithSerch.getIssues().stream()
                .filter(serch -> !serch.getIsRead())
                .filter(serch -> !serch.getSender().equals(String.valueOf(authUtil.getUser().getId())))
                .forEach(serch -> {
                    serch.setIsRead(true);
                    serch.setUpdatedAt(TimeUtil.now());
                    issueRepository.save(serch);
                });

        return new ApiResponse<>(getTickets(speakWithSerch.getIssues().size() - 1, null));
    }

    @Override
    @Transactional
    public void removeOldContents() {
        List<SpeakWithSerch> list = speakWithSerchRepository.findByCreatedAtBefore(TimeUtil.now().minusYears(5));
        if(list != null && !list.isEmpty()) {
            speakWithSerchRepository.deleteAll(list);
        }
    }
}
