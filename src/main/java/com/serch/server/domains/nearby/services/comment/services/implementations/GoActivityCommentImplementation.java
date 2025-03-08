package com.serch.server.domains.nearby.services.comment.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.models.go.activity.GoActivityComment;
import com.serch.server.domains.nearby.services.comment.requests.GoActivityCommentRequest;
import com.serch.server.domains.nearby.services.comment.responses.GoActivityCommentResponse;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.repositories.go.GoActivityCommentRepository;
import com.serch.server.domains.nearby.repositories.go.GoActivityRepository;
import com.serch.server.domains.nearby.services.comment.services.GoActivityCommentService;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class GoActivityCommentImplementation implements GoActivityCommentService {
    private final GoActivityRepository goActivityRepository;
    private final GoActivityCommentRepository goActivityCommentRepository;
    private final AuthUtil authUtil;

    @Override
    public ApiResponse<GoActivityCommentResponse> comment(GoActivityCommentRequest request) {
        GoUser user = authUtil.getGoUser();
        GoActivity activity = goActivityRepository.findById(request.getId()).orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.isEnded()) {
            AtomicReference<GoActivityComment> goComment = new AtomicReference<>();
            goActivityCommentRepository.findByUserAndActivity(user.getId(), activity.getId()).ifPresentOrElse(comment -> {
                comment.setActivity(activity);
                comment.setComment(request.getComment());
                comment.setUpdatedAt(TimeUtil.now());
                goComment.set(goActivityCommentRepository.save(comment));
            }, () -> {
                GoActivityComment comment = new GoActivityComment();
                comment.setUser(user);
                comment.setActivity(activity);
                comment.setComment(request.getComment());
                goComment.set(goActivityCommentRepository.save(comment));
            });

            return new ApiResponse<>(
                    "Comment successfully received",
                    getComment(goComment.get()),
                    HttpStatus.OK
            );
        } else {
            throw new SerchException("You cannot comment on an active activity. This activity must end before doing this");
        }
    }

    @Override
    public ApiResponse<List<GoActivityCommentResponse>> getComments(Integer page, Integer size, String id) {
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.isEnded()) {
            Pageable pageable = PageRequest.of(
                    page == null ? 0 : page,
                    size == null ? 20 : size,
                    Sort.by(Sort.Direction.ASC, "createdAt")
            );

            Page<GoActivityComment> comments = goActivityCommentRepository.findByActivity_Id(activity.getId(), pageable);

            if(comments.isEmpty()) {
                return new ApiResponse<>(new ArrayList<>());
            } else {
                return new ApiResponse<>(comments.getContent().stream().map(this::getComment).toList());
            }
        }

        return new ApiResponse<>(new ArrayList<>());
    }

    private GoActivityCommentResponse getComment(GoActivityComment comment) {
        GoActivityCommentResponse response = GoMapper.instance.comment(comment);

        authUtil.getOptionalGoUser().ifPresentOrElse(
                user -> response.setIsCurrentUser(comment.getUser().getId().equals(user.getId())),
                () -> response.setIsCurrentUser(false)
        );

        return response;
    }
}