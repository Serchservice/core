package com.serch.server.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.models.MediaBlog;
import com.serch.server.services.media.repositories.MediaBlogRepository;
import com.serch.server.services.media.responses.MediaBlogResponse;
import com.serch.server.services.media.services.MediaBlogImplementation;
import com.serch.server.services.media.services.MediaBlogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {MediaBlogController.class})
@ExtendWith(SpringExtension.class)
class MediaBlogControllerTest {
    @Autowired
    private MediaBlogController blogController;

    @MockBean
    private MediaBlogService blogService;

    /**
     * Method under test: {@link MediaBlogController#fetchAllBlogs(Integer)}
     */
    @Test
    void testFetchAllBlogs() throws Exception {
        // Arrange
        when(blogService.findAllBlogs(Mockito.<Integer>any())).thenReturn(new ApiResponse<>("Not all who wander are lost"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/company/media/blogs");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(blogController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"data\":null}"));
    }

    /**
     * Method under test: {@link MediaBlogController#fetchBlog(String)}
     */
    @Test
    void testFetchBlog() {
        // Arrange
        MediaBlog blog = new MediaBlog();
        blog.setBlog("MediaBlog");
        blog.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        blog.setImage("Image");
        blog.setKey("Key");
        blog.setRegion("us-east-2");
        blog.setTitle("Dr");
        blog.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<MediaBlog> ofResult = Optional.of(blog);
        MediaBlogRepository blogRepository = mock(MediaBlogRepository.class);
        when(blogRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ResponseEntity<ApiResponse<MediaBlogResponse>> actualFetchBlogResult = (new MediaBlogController(
                new MediaBlogImplementation(blogRepository))).fetchBlog("Key");

        // Assert
        verify(blogRepository).findById(Mockito.any());
        ApiResponse<MediaBlogResponse> body = actualFetchBlogResult.getBody();
        assert body != null;
        assertEquals("MediaBlog found", body.getMessage());
        MediaBlogResponse data = body.getData();
        assertEquals("MediaBlog", data.getBlog());
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getKey());
        assertEquals("Thursday, 1\" January | us-east-2", data.getRegion());
        assertEquals(200, body.getCode().intValue());
        assertEquals(200, actualFetchBlogResult.getStatusCode().value());
        assertEquals(HttpStatus.OK, body.getStatus());
        assertTrue(actualFetchBlogResult.hasBody());
        assertTrue(actualFetchBlogResult.getHeaders().isEmpty());
    }
}
