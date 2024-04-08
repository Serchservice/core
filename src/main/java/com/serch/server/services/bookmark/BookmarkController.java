package com.serch.server.services.bookmark;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.bookmark.services.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> bookmarks() {
        var response = bookmarkService.bookmarks();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(@RequestBody AddBookmarkRequest request) {
        var response = bookmarkService.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> remove(@RequestParam String id) {
        var response = bookmarkService.remove(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
