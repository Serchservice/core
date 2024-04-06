package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.media.MediaLegalException;
import com.serch.server.enums.media.LegalLOB;
import com.serch.server.models.media.MediaLegal;
import com.serch.server.repositories.media.MediaLegalRepository;
import com.serch.server.services.media.responses.MediaLegalGroupResponse;
import com.serch.server.services.media.responses.MediaLegalResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MediaLegalImplementation.class})
@ExtendWith(SpringExtension.class)
class MediaLegalImplementationTest {
    @Autowired
    private MediaLegalImplementation legalImplementation;

    @MockBean
    private MediaLegalRepository legalRepository;

    /**
     * Method under test: {@link MediaLegalImplementation#fetchAllLegals()}
     */
    @Test
    void testFetchAllLegals() {
        // Arrange
        ArrayList<MediaLegal> legalList = new ArrayList<>();
        when(legalRepository.findAll()).thenReturn(legalList);

        // Act
        ApiResponse<List<MediaLegalGroupResponse>> actualFetchAllLegalsResult = legalImplementation.fetchAllLegals();

        // Assert
        verify(legalRepository).findAll();
        assertEquals("Legals fetched", actualFetchAllLegalsResult.getMessage());
        assertEquals(200, actualFetchAllLegalsResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFetchAllLegalsResult.getStatus());
        assertTrue(legalList.isEmpty());
        assertTrue(actualFetchAllLegalsResult.getData().isEmpty());
    }

    /**
     * Method under test: {@link MediaLegalImplementation#fetchAllLegals()}
     */
    @Test
    void testFetchAllLegals2() {
        // Arrange
        MediaLegal legal = new MediaLegal();
        legal.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        legal.setImage("Legals fetched");
        legal.setId("Legals fetched");
        legal.setLegal("Legals fetched");
        legal.setLob(LegalLOB.USER);
        legal.setTitle("Dr");
        legal.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<MediaLegal> legalList = new ArrayList<>();
        legalList.add(legal);
        when(legalRepository.findAll()).thenReturn(legalList);

        // Act
        ApiResponse<List<MediaLegalGroupResponse>> actualFetchAllLegalsResult = legalImplementation.fetchAllLegals();

        // Assert
        verify(legalRepository).findAll();
        assertEquals("Legals fetched", actualFetchAllLegalsResult.getMessage());
        List<MediaLegalGroupResponse> data = actualFetchAllLegalsResult.getData();
        assertEquals(1, data.size());
        MediaLegalGroupResponse getResult = data.get(0);
        assertEquals("Request/User", getResult.getLineOfBusiness());
        assertEquals(1, getResult.getLegalList().size());
        assertEquals(200, actualFetchAllLegalsResult.getCode().intValue());
        assertEquals(LegalLOB.USER, getResult.getLob());
        assertEquals(HttpStatus.OK, actualFetchAllLegalsResult.getStatus());
    }

    /**
     * Method under test: {@link MediaLegalImplementation#fetchAllLegals()}
     */
    @Test
    void testFetchAllLegals3() {
        // Arrange
        MediaLegal legal = new MediaLegal();
        legal.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        legal.setImage("Legals fetched");
        legal.setId("Legals fetched");
        legal.setLegal("Legals fetched");
        legal.setLob(LegalLOB.USER);
        legal.setTitle("Dr");
        legal.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MediaLegal legal2 = new MediaLegal();
        legal2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        legal2.setImage("Image");
        legal2.setId("Key");
        legal2.setLegal("MediaLegal");
        legal2.setLob(LegalLOB.GUEST);
        legal2.setTitle("Mr");
        legal2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<MediaLegal> legalList = new ArrayList<>();
        legalList.add(legal);
        legalList.add(legal2);
        when(legalRepository.findAll()).thenReturn(legalList);

        // Act
        ApiResponse<List<MediaLegalGroupResponse>> actualFetchAllLegalsResult = legalImplementation.fetchAllLegals();

        // Assert
        verify(legalRepository).findAll();
        assertEquals("Legals fetched", actualFetchAllLegalsResult.getMessage());
        List<MediaLegalGroupResponse> data = actualFetchAllLegalsResult.getData();
        assertEquals(2, data.size());
        assertEquals(HttpStatus.OK, actualFetchAllLegalsResult.getStatus());
    }

    /**
     * Method under test: {@link MediaLegalImplementation#fetchAllLegals()}
     */
    @Test
    void testFetchAllLegals4() {
        // Arrange
        when(legalRepository.findAll()).thenThrow(new MediaLegalException("An error occurred"));

        // Act and Assert
        assertThrows(MediaLegalException.class, () -> legalImplementation.fetchAllLegals());
        verify(legalRepository).findAll();
    }

    /**
     * Method under test: {@link MediaLegalImplementation#fetchLegal(String)}
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
        when(legalRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<MediaLegalResponse> actualFetchLegalResult = legalImplementation.fetchLegal("Key");

        // Assert
        verify(legalRepository).findById(Mockito.any());
        MediaLegalResponse data = actualFetchLegalResult.getData();
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getId());
        assertEquals("Legal Document fetched", actualFetchLegalResult.getMessage());
        assertEquals("MediaLegal", data.getLegal());
        assertEquals("Request/User", data.getLineOfBusiness());
        assertEquals(200, actualFetchLegalResult.getCode().intValue());
        assertEquals(LegalLOB.USER, data.getLob());
        assertEquals(HttpStatus.OK, actualFetchLegalResult.getStatus());
    }

    /**
     * Method under test: {@link MediaLegalImplementation#fetchLegal(String)}
     */
    @Test
    void testFetchLegal2() {
        // Arrange
        Optional<MediaLegal> emptyResult = Optional.empty();
        when(legalRepository.findById(Mockito.any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(MediaLegalException.class, () -> legalImplementation.fetchLegal("Key"));
        verify(legalRepository).findById(Mockito.any());
    }
}
