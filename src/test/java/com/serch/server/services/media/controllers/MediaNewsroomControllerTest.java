package com.serch.server.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.models.MediaNewsroom;
import com.serch.server.services.media.repositories.MediaNewsroomRepository;
import com.serch.server.services.media.responses.MediaNewsroomResponse;
import com.serch.server.services.media.services.MediaNewsroomImplementation;
import com.serch.server.services.media.services.MediaNewsroomService;
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

@ContextConfiguration(classes = {MediaNewsroomController.class})
@ExtendWith(SpringExtension.class)
class MediaNewsroomControllerTest {
    @Autowired
    private MediaNewsroomController newsroomController;

    @MockBean
    private MediaNewsroomService newsroomService;

    /**
     * Method under test: {@link MediaNewsroomController#fetchAllNews(Integer)}
     */
    @Test
    void testFetchAllNews() throws Exception {
        // Arrange
        when(newsroomService.findAllNews(Mockito.<Integer>any()))
                .thenReturn(new ApiResponse<>("Not all who wander are lost"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/company/media/newsroom");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(newsroomController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"data\":null}"));
    }

    /**
     * Method under test: {@link MediaNewsroomController#fetchNews(String)}
     */
    @Test
    void testFetchNews() {
        // Arrange
        MediaNewsroom newsroom = new MediaNewsroom();
        newsroom.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        newsroom.setImage("Image");
        newsroom.setId("Key");
        newsroom.setNews("News");
        newsroom.setRegion("us-east-2");
        newsroom.setTitle("Dr");
        newsroom.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<MediaNewsroom> ofResult = Optional.of(newsroom);
        MediaNewsroomRepository newsroomRepository = mock(MediaNewsroomRepository.class);
        when(newsroomRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ResponseEntity<ApiResponse<MediaNewsroomResponse>> actualFetchNewsResult = (new MediaNewsroomController(
                new MediaNewsroomImplementation(newsroomRepository))).fetchNews("Key");

        // Assert
        verify(newsroomRepository).findById(Mockito.any());
        ApiResponse<MediaNewsroomResponse> body = actualFetchNewsResult.getBody();
        assert body != null;
        MediaNewsroomResponse data = body.getData();
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getId());
        assertEquals("News fetched", body.getMessage());
        assertEquals("News", data.getNews());
        assertEquals("Thursday, 1\" January | us-east-2", data.getRegion());
        assertEquals(200, body.getCode().intValue());
        assertEquals(200, actualFetchNewsResult.getStatusCode().value());
        assertEquals(HttpStatus.OK, body.getStatus());
        assertTrue(actualFetchNewsResult.hasBody());
        assertTrue(actualFetchNewsResult.getHeaders().isEmpty());
    }
}
