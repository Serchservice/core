package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.MediaNewsroomException;
import com.serch.server.services.media.models.MediaNewsroom;
import com.serch.server.services.media.repositories.MediaNewsroomRepository;
import com.serch.server.services.media.responses.MediaNewsroomResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = MediaNewsroomImplementation.class)
@ExtendWith(SpringExtension.class)
class MediaNewsroomImplementationTest {
    @MockBean
    private MediaNewsroomRepository newsroomRepository;

    @Autowired
    private MediaNewsroomImplementation newsroomImplementation;

    /**
     * Method under test: {@link MediaNewsroomImplementation#findNews(String)}
     */
    @Test
    void testFindNews() {
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
        when(newsroomRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<MediaNewsroomResponse> actualFindNewsResult = newsroomImplementation.findNews("Key");

        // Assert
        verify(newsroomRepository).findById(Mockito.any());
        MediaNewsroomResponse data = actualFindNewsResult.getData();
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getId());
        assertEquals("News fetched", actualFindNewsResult.getMessage());
        assertEquals("News", data.getNews());
        assertEquals("Thursday, 1\" January | us-east-2", data.getRegion());
        assertEquals(200, actualFindNewsResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFindNewsResult.getStatus());
    }

    /**
     * Method under test: {@link MediaNewsroomImplementation#findNews(String)}
     */
    @Test
    void testFindNews2() {
        // Arrange
        MediaNewsroom newsroom = mock(MediaNewsroom.class);
        when(newsroom.getImage()).thenReturn("Image");
        when(newsroom.getId()).thenReturn("Key");
        when(newsroom.getNews()).thenReturn("News");
        when(newsroom.getRegion()).thenReturn("us-east-2");
        when(newsroom.getTitle()).thenReturn("Dr");
        when(newsroom.getCreatedAt()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
        doNothing().when(newsroom).setCreatedAt(Mockito.any());
        doNothing().when(newsroom).setUpdatedAt(Mockito.any());
        doNothing().when(newsroom).setImage(Mockito.any());
        doNothing().when(newsroom).setId(Mockito.any());
        doNothing().when(newsroom).setNews(Mockito.any());
        doNothing().when(newsroom).setRegion(Mockito.any());
        doNothing().when(newsroom).setTitle(Mockito.any());
        newsroom.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        newsroom.setImage("Image");
        newsroom.setId("Key");
        newsroom.setNews("News");
        newsroom.setRegion("us-east-2");
        newsroom.setTitle("Dr");
        newsroom.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<MediaNewsroom> ofResult = Optional.of(newsroom);
        when(newsroomRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<MediaNewsroomResponse> actualFindNewsResult = newsroomImplementation.findNews("Key");

        // Assert
        verify(newsroom).getCreatedAt();
        verify(newsroom).setCreatedAt(Mockito.any());
        verify(newsroom).setUpdatedAt(Mockito.any());
        verify(newsroom).getImage();
        verify(newsroom).getId();
        verify(newsroom).getNews();
        verify(newsroom, atLeast(1)).getRegion();
        verify(newsroom).getTitle();
        verify(newsroom).setImage(Mockito.any());
        verify(newsroom).setId(Mockito.any());
        verify(newsroom).setNews(Mockito.any());
        verify(newsroom).setRegion(Mockito.any());
        verify(newsroom).setTitle(Mockito.any());
        verify(newsroomRepository).findById(Mockito.any());
        MediaNewsroomResponse data = actualFindNewsResult.getData();
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getId());
        assertEquals("News fetched", actualFindNewsResult.getMessage());
        assertEquals("News", data.getNews());
        assertEquals("Thursday, 1\" January | us-east-2", data.getRegion());
        assertEquals(200, actualFindNewsResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFindNewsResult.getStatus());
    }

    /**
     * Method under test: {@link MediaNewsroomImplementation#findNews(String)}
     */
    @Test
    void testFindNews3() {
        // Arrange
        Optional<MediaNewsroom> emptyResult = Optional.empty();
        when(newsroomRepository.findById(Mockito.any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(MediaNewsroomException.class, () -> newsroomImplementation.findNews("Key"));
        verify(newsroomRepository).findById(Mockito.any());
    }

    /**
     * Method under test: {@link MediaNewsroomImplementation#findAllNews(Integer)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testFindAllNews() {
        // TODO: Complete this test.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@c7c4bf7 testClass = com.serch.media.services.DiffblueFakeClass1, locations = [], classes = [com.serch.media.MediaApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@425f07bb, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@53b0e0f, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@690f4565, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@63d0ddd6, org.springframework.boot.test.context.SpringBootTestAnnotation@f0ecd31], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.Optional.map(Optional.java:260)
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange and Act
        newsroomImplementation.findAllNews(1);
    }
}
