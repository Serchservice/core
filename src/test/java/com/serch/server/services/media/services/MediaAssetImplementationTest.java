package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.repositories.MediaAssetRepository;
import com.serch.server.services.media.responses.MediaAssetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MediaAssetImplementation.class})
@ExtendWith(SpringExtension.class)
class MediaAssetImplementationTest {
    @Autowired
    private MediaAssetImplementation assetImplementation;

    @MockBean
    private MediaAssetRepository assetRepository;

    /**
     * Method under test: {@link MediaAssetImplementation#fetchAllAssets()}
     */
    @Test
    void testFetchAllAssets() {
        // Arrange
        when(assetRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<MediaAssetResponse>> actualFetchAllAssetResult = assetImplementation.fetchAllAssets();

        // Assert
        verify(assetRepository).findAll();
        assertEquals("Fetched assets", actualFetchAllAssetResult.getMessage());
        assertEquals(200, actualFetchAllAssetResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFetchAllAssetResult.getStatus());
        assertTrue(actualFetchAllAssetResult.getData().isEmpty());
    }
}
