package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.MediaBlogException;
import com.serch.server.services.media.models.MediaBlog;
import com.serch.server.services.media.repositories.MediaBlogRepository;
import com.serch.server.services.media.responses.MediaBlogResponse;
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

@ContextConfiguration(classes = MediaBlogImplementation.class)
@ExtendWith(SpringExtension.class)
class MediaBlogImplementationTest {
    @MockBean
    private MediaBlogRepository blogRepository;

    @Autowired
    private MediaBlogImplementation blogImplementation;

    /**
     * Method under test: {@link MediaBlogImplementation#findBlog(String)}
     */
    @Test
    void testFindBlog() {
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
        when(blogRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<MediaBlogResponse> actualFindBlogResult = blogImplementation.findBlog("Key");

        // Assert
        verify(blogRepository).findById(Mockito.any());
        assertEquals("MediaBlog found", actualFindBlogResult.getMessage());
        MediaBlogResponse data = actualFindBlogResult.getData();
        assertEquals("MediaBlog", data.getBlog());
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getKey());
        assertEquals("Thursday, 1\" January | us-east-2", data.getRegion());
        assertEquals(200, actualFindBlogResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFindBlogResult.getStatus());
    }

    /**
     * Method under test: {@link MediaBlogImplementation#findBlog(String)}
     */
    @Test
    void testFindBlog2() {
        // Arrange
        MediaBlog blog = mock(MediaBlog.class);
        when(blog.getBlog()).thenReturn("MediaBlog");
        when(blog.getImage()).thenReturn("Image");
        when(blog.getKey()).thenReturn("Key");
        when(blog.getRegion()).thenReturn("us-east-2");
        when(blog.getTitle()).thenReturn("Dr");
        when(blog.getCreatedAt()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
        doNothing().when(blog).setCreatedAt(Mockito.any());
        doNothing().when(blog).setUpdatedAt(Mockito.any());
        doNothing().when(blog).setBlog(Mockito.any());
        doNothing().when(blog).setImage(Mockito.any());
        doNothing().when(blog).setKey(Mockito.any());
        doNothing().when(blog).setRegion(Mockito.any());
        doNothing().when(blog).setTitle(Mockito.any());
        blog.setBlog("MediaBlog");
        blog.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        blog.setImage("Image");
        blog.setKey("Key");
        blog.setRegion("us-east-2");
        blog.setTitle("Dr");
        blog.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<MediaBlog> ofResult = Optional.of(blog);
        when(blogRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<MediaBlogResponse> actualFindBlogResult = blogImplementation.findBlog("Key");

        // Assert
        verify(blog).getCreatedAt();
        verify(blog).setCreatedAt(Mockito.any());
        verify(blog).setUpdatedAt(Mockito.any());
        verify(blog).getBlog();
        verify(blog).getImage();
        verify(blog).getKey();
        verify(blog, atLeast(1)).getRegion();
        verify(blog).getTitle();
        verify(blog).setBlog(Mockito.any());
        verify(blog).setImage(Mockito.any());
        verify(blog).setKey(Mockito.any());
        verify(blog).setRegion(Mockito.any());
        verify(blog).setTitle(Mockito.any());
        verify(blogRepository).findById(Mockito.any());
        assertEquals("MediaBlog found", actualFindBlogResult.getMessage());
        MediaBlogResponse data = actualFindBlogResult.getData();
        assertEquals("MediaBlog", data.getBlog());
        assertEquals("Dr", data.getTitle());
        assertEquals("Image", data.getImage());
        assertEquals("Key", data.getKey());
        assertEquals("Thursday, 1\" January | us-east-2", data.getRegion());
        assertEquals(200, actualFindBlogResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFindBlogResult.getStatus());
    }

    /**
     * Method under test: {@link MediaBlogImplementation#findBlog(String)}
     */
    @Test
    void testFindBlog3() {
        // Arrange
        Optional<MediaBlog> emptyResult = Optional.empty();
        when(blogRepository.findById(Mockito.any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(MediaBlogException.class, () -> blogImplementation.findBlog("Key"));
        verify(blogRepository).findById(Mockito.any());
    }

    /**
     * Method under test: {@link MediaBlogImplementation#findAllBlogs(Integer)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testFindAllBlogs() {
        // TODO: Complete this test.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@7dfa4cd4 testClass = com.serch.media.services.DiffblueFakeClass1, locations = [], classes = [com.serch.media.MediaApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@54d2dba9, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@46956146, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@1ffd9d20, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@3cb1402e, org.springframework.boot.test.context.SpringBootTestAnnotation@2974babe], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.Optional.map(Optional.java:260)
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange and Act
        blogImplementation.findAllBlogs(1);
    }
}
