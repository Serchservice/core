package com.serch.server.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.LegalLOB;
import com.serch.server.services.media.models.MediaLegal;
import com.serch.server.services.media.repositories.MediaLegalRepository;
import com.serch.server.services.media.responses.MediaLegalResponse;
import com.serch.server.services.media.services.MediaLegalImplementation;
import com.serch.server.services.media.services.MediaLegalService;
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

@ContextConfiguration(classes = {MediaLegalController.class})
@ExtendWith(SpringExtension.class)
class MediaLegalControllerTest {
    @Autowired
    private MediaLegalController legalController;

    @MockBean
    private MediaLegalService legalService;

    /**
     * Method under test: {@link MediaLegalController#fetchLegal(String)}
     */
    @Test
    void testFetchLegal() {
        // Arrange
        MediaLegal legal = new MediaLegal();
        legal.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        legal.setImage("Image");
        legal.setId("Key");
        legal.setLegal("MediaLegal");
        legal.setLob(LegalLOB.USER);
        legal.setTitle("Dr");
        legal.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<MediaLegal> ofResult = Optional.of(legal);
        MediaLegalRepository legalRepository = mock(MediaLegalRepository.class);
        when(legalRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ResponseEntity<ApiResponse<MediaLegalResponse>> actualFetchLegalResult = (new MediaLegalController(
                new MediaLegalImplementation(legalRepository))).fetchLegal("Key");

        // Assert
        verify(legalRepository).findById(Mockito.any());
        ApiResponse<MediaLegalResponse> body = actualFetchLegalResult.getBody();
        assert body != null;
        MediaLegalResponse data = body.getData();
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getId());
        assertEquals("Legal Document fetched", body.getMessage());
        assertEquals("MediaLegal", data.getLegal());
        assertEquals("Request/User", data.getLineOfBusiness());
        assertEquals(200, body.getCode().intValue());
        assertEquals(200, actualFetchLegalResult.getStatusCode().value());
        assertEquals(LegalLOB.USER, data.getLob());
        assertEquals(HttpStatus.OK, body.getStatus());
        assertTrue(actualFetchLegalResult.hasBody());
        assertTrue(actualFetchLegalResult.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link MediaLegalController#fetchAllLegals()}
     */
    @Test
    void testFetchAllLegals() throws Exception {
        // Arrange
        when(legalService.fetchAllLegals()).thenReturn(new ApiResponse<>("Not all who wander are lost"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/company/media/legal");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(legalController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"data\":null}"));
    }
}
