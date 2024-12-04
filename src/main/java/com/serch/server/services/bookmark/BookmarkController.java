package com.serch.server.services.bookmark;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.bookmark.services.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> bookmarks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        var response = bookmarkService.bookmarks(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> add(@RequestBody AddBookmarkRequest request) {
        var response = bookmarkService.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> remove(@RequestParam String id) {
        var response = bookmarkService.remove(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
