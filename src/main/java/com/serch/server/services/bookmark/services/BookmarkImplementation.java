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
        Optional<Bookmark> existing = bookmarkRepository.findByUser_SerchIdAndProvider_SerchId(
                userUtil.getUser().getId(), provider.getSerchId()
        );

        if(existing.isPresent()) {
            throw new BookmarkException("You have already bookmarked this provider");
        } else {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setProvider(provider);
            bookmarkRepository.save(bookmark);
            return new ApiResponse<>("Bookmark added", HttpStatus.CREATED);
        }
    }

    @Override
    public ApiResponse<String> remove(String bookmarkId) {
        bookmarkRepository.findByBookmarkIdAndUser_SerchId(bookmarkId, userUtil.getUser().getId())
                .ifPresentOrElse(
                        bookmarkRepository::delete,
                        () -> {
                            throw new BookmarkException("Bookmark does not exist");
                        });
        return new ApiResponse<>("Bookmark removed", HttpStatus.OK);
    }

    @Override
    public ApiResponse<List<BookmarkResponse>> bookmarks() {
        List<BookmarkResponse> list = bookmarkRepository.findBySerchId(userUtil.getUser().getId())
                .stream()
                .sorted(Comparator.comparing(Bookmark::getCreatedAt))
                .map(bookmark -> {
                    BookmarkResponse response = new BookmarkResponse();
                    response.setId(bookmark.getBookmarkId());
                    response.setCategory(
                            userUtil.getUser().isUser(bookmark.getUser().getSerchId())
                                    ? bookmark.getProvider().getCategory().getType()
                                    : bookmark.getUser().getCategory().getType()
                    );
                    response.setName(
                            userUtil.getUser().isUser(bookmark.getUser().getSerchId())
                                    ? bookmark.getProvider().getFullName()
                                    : bookmark.getUser().getFullName()
                    );
                    response.setAvatar(
                            userUtil.getUser().isUser(bookmark.getUser().getSerchId())
                                    ? bookmark.getProvider().getAvatar()
                                    : bookmark.getUser().getAvatar()
                    );
                    response.setRating(
                            userUtil.getUser().isUser(bookmark.getUser().getSerchId())
                                    ? bookmark.getProvider().getRating()
                                    : bookmark.getUser().getRating()
                    );
                    response.setLastSeen(
                            userUtil.getUser().isUser(bookmark.getUser().getSerchId())
                                    ? TimeUtil.formatLastSeen(bookmark.getProvider().getUser().getLastSeen())
                                    : TimeUtil.formatLastSeen(bookmark.getUser().getUser().getLastSeen())
                    );
                    return response;
                })
                .toList();
        return new ApiResponse<>(list);
    }
}
