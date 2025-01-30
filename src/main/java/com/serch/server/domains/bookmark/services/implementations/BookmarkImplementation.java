package com.serch.server.domains.bookmark.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.bookmark.services.BookmarkService;
import com.serch.server.exceptions.others.BookmarkException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.bookmark.Bookmark;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.bookmark.BookmarkRepository;
import com.serch.server.domains.bookmark.request.AddBookmarkRequest;
import com.serch.server.domains.bookmark.response.BookmarkResponse;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing bookmarks.
 * It implements its wrapper interface {@link BookmarkService}.
 *
 * @see AuthUtil
 * @see ProfileRepository
 * @see BookmarkRepository
 */
@Service
@RequiredArgsConstructor
public class BookmarkImplementation implements BookmarkService {
    private final AuthUtil authUtil;
    private final ProfileRepository profileRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public ApiResponse<String> add(AddBookmarkRequest request) {
        Profile user = profileRepository.findById(authUtil.getUser().getId())
                .orElseThrow(() -> new BookmarkException("User profile not found"));
        Profile provider = profileRepository.findById(request.getUser())
                .orElseThrow(() -> new BookmarkException("Provider profile not found"));
        Optional<Bookmark> existing = bookmarkRepository.findByUser_IdAndProvider_Id(authUtil.getUser().getId(), provider.getId());

        if(existing.isPresent()) {
            throw new BookmarkException("You have already bookmarked this provider");
        } else {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setProvider(provider);
            bookmark = bookmarkRepository.save(bookmark);

            return new ApiResponse<>("Bookmark added", bookmark.getBookmarkId(), HttpStatus.CREATED);
        }
    }

    @Override
    public ApiResponse<String> remove(String bookmarkId) {
        bookmarkRepository.findByBookmarkIdAndUser_Id(bookmarkId, authUtil.getUser().getId()).ifPresentOrElse(
                bookmarkRepository::delete, () -> {
                    throw new BookmarkException("Bookmark does not exist");
                }
        );

        return new ApiResponse<>("Bookmark removed", HttpStatus.OK);
    }

    @Override
    public ApiResponse<List<BookmarkResponse>> bookmarks(Integer page, Integer size) {
        List<BookmarkResponse> list = new ArrayList<>();

        Page<Bookmark> bookmarks = bookmarkRepository.findByUserId(authUtil.getUser().getId(), HelperUtil.getPageable(page, size));
        if(!bookmarks.isEmpty()) {
            list = bookmarks.getContent()
                    .stream()
                    .sorted(Comparator.comparing(Bookmark::getCreatedAt))
                    .map(bookmark -> {
                        BookmarkResponse response = new BookmarkResponse();
                        response.setId(bookmark.getBookmarkId());
                        response.setCategory(
                                authUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getCategory().getType()
                                        : bookmark.getUser().getCategory().getType()
                        );
                        response.setName(
                                authUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getFullName()
                                        : bookmark.getUser().getFullName()
                        );
                        response.setAvatar(
                                authUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getAvatar()
                                        : bookmark.getUser().getAvatar()
                        );
                        response.setRating(
                                authUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? bookmark.getProvider().getRating()
                                        : bookmark.getUser().getRating()
                        );
                        response.setLastSignedIn(
                                authUtil.getUser().isUser(bookmark.getUser().getId())
                                        ? TimeUtil.formatLastSignedIn(bookmark.getProvider().getUser().getLastSignedIn(), bookmark.getProvider().getUser().getTimezone(), true)
                                        : TimeUtil.formatLastSignedIn(bookmark.getUser().getUser().getLastSignedIn(), bookmark.getUser().getUser().getTimezone(), true)
                        );
                        response.setUser(
                                authUtil.getUser().isUser(bookmark.getUser().getId())
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
