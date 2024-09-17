package com.serch.server.services.bookmark.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.BookmarkException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.bookmark.Bookmark;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.bookmark.BookmarkRepository;
import com.serch.server.services.bookmark.AddBookmarkRequest;
import com.serch.server.services.bookmark.BookmarkResponse;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service implementation for managing bookmarks.
 * It implements its wrapper interface {@link BookmarkService}.
 *
 * @see UserUtil
 * @see ProfileRepository
 * @see BookmarkRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
public class BookmarkImplementation implements BookmarkService {
    private final UserUtil userUtil;
    private final ProfileRepository profileRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public ApiResponse<String> add(AddBookmarkRequest request) {
        Profile user = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new BookmarkException("User profile not found"));
        Profile provider = profileRepository.findById(request.getUser())
                .orElseThrow(() -> new BookmarkException("Provider profile not found"));
        Optional<Bookmark> existing = bookmarkRepository.findByUser_IdAndProvider_Id(
                userUtil.getUser().getId(), provider.getId()
        );

        if(existing.isPresent()) {
            throw new BookmarkException("You have already bookmarked this provider");
        } else {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setProvider(provider);
            Bookmark saved = bookmarkRepository.save(bookmark);
            return new ApiResponse<>("Bookmark added", saved.getBookmarkId(), HttpStatus.CREATED);
        }
    }

    @Override
    public ApiResponse<String> remove(String bookmarkId) {
        bookmarkRepository.findByBookmarkIdAndUser_Id(bookmarkId, userUtil.getUser().getId())
                .ifPresentOrElse(
                        bookmarkRepository::delete,
                        () -> {
                            throw new BookmarkException("Bookmark does not exist");
                        });
        return new ApiResponse<>("Bookmark removed", HttpStatus.OK);
    }

    @Override
    public ApiResponse<List<BookmarkResponse>> bookmarks() {
        List<BookmarkResponse> list = new ArrayList<>();

        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userUtil.getUser().getId());
        if(!bookmarks.isEmpty()) {
            list = bookmarks.stream()
                    .sorted(Comparator.comparing(Bookmark::getCreatedAt))
                    .map(bookmark -> {
                        BookmarkResponse response = new BookmarkResponse();
                        response.setId(bookmark.getBookmarkId());
                        response.setCategory(
                                userUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getCategory().getType()
                                        : bookmark.getUser().getCategory().getType()
                        );
                        response.setName(
                                userUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getFullName()
                                        : bookmark.getUser().getFullName()
                        );
                        response.setAvatar(
                                userUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getAvatar()
                                        : bookmark.getUser().getAvatar()
                        );
                        response.setRating(
                                userUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getRating()
                                        : bookmark.getUser().getRating()
                        );
                        response.setLastSignedIn(
                                userUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? TimeUtil.formatLastSignedIn(bookmark.getProvider().getUser().getLastSignedIn(), bookmark.getProvider().getUser().getTimezone(), true)
                                        : TimeUtil.formatLastSignedIn(bookmark.getUser().getUser().getLastSignedIn(), bookmark.getUser().getUser().getTimezone(), true)
                        );
                        response.setUser(
                                userUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getUser().getId()
                                        : bookmark.getUser().getUser().getId()
                        );
                        return response;
                    })
                    .toList();
        }
        return new ApiResponse<>(list);
    }
}
