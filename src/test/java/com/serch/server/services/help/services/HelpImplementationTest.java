package com.serch.server.services.help.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.HelpException;
import com.serch.server.models.help.*;
import com.serch.server.repositories.help.*;
import com.serch.server.services.help.requests.HelpAskRequest;
import com.serch.server.services.help.response.*;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {HelpImplementation.class})
@ExtendWith(SpringExtension.class)
class HelpImplementationTest {
    @MockBean
    private HelpAskRepository askRepository;

    @MockBean
    private HelpGroupRepository groupRepository;

    @MockBean
    private HelpCategoryRepository categoryRepository;

    @Autowired
    private HelpImplementation helpImplementation;

    @MockBean
    private HelpRepository helpRepository;

    @MockBean
    private HelpSectionRepository sectionRepository;

    /**
     * Method under test: {@link HelpImplementation#fetchCategories()}
     */
    @Test
    void testFetchCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<HelpCategoryResponse>> actualFetchCategoriesResult = helpImplementation.fetchCategories();

        // Assert
        verify(categoryRepository).findAll();
        assertEquals("Categories fetched", actualFetchCategoriesResult.getMessage());
        assertEquals(200, actualFetchCategoriesResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFetchCategoriesResult.getStatus());
        assertTrue(actualFetchCategoriesResult.getData().isEmpty());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchCategories()}
     */
    @Test
    void testFetchCategories2() {
        // Arrange
        when(categoryRepository.findAll()).thenThrow(new HelpException("An error occurred"));

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.fetchCategories());
        verify(categoryRepository).findAll();
    }

    /**
     * Method under test: {@link HelpImplementation#fetchSections(String)}
     */
    @Test
    void testFetchSections() {
        // Arrange
        HelpCategory category = new HelpCategory();
        category.setCategory("HelpCategory");
        category.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        category.setImage("Image");
        category.setId("Key");
        category.setSections(new ArrayList<>());
        category.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<HelpCategory> ofResult = Optional.of(category);
        when(categoryRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<List<HelpSectionResponse>> actualFetchSectionsResult = helpImplementation.fetchSections("Key");

        // Assert
        verify(categoryRepository).findById(Mockito.any());
        assertEquals("Sections fetched", actualFetchSectionsResult.getMessage());
        assertEquals(200, actualFetchSectionsResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFetchSectionsResult.getStatus());
        assertTrue(actualFetchSectionsResult.getData().isEmpty());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchSections(String)}
     */
    @Test
    void testFetchSections2() {
        // Arrange
        Optional<HelpCategory> emptyResult = Optional.empty();
        when(categoryRepository.findById(Mockito.any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.fetchSections("Key"));
        verify(categoryRepository).findById(Mockito.any());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchSections(String)}
     */
    @Test
    void testFetchSections3() {
        // Arrange
        when(categoryRepository.findById(Mockito.any())).thenThrow(new HelpException("An error occurred"));

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.fetchSections("Key"));
        verify(categoryRepository).findById(Mockito.any());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchGroups(String, String)}
     */
    @Test
    void testFetchGroups() {
        // Arrange
        HelpCategory category = new HelpCategory();
        category.setCategory("HelpCategory");
        category.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        category.setImage("Image");
        category.setId("Key");
        category.setSections(new ArrayList<>());
        category.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        HelpSection section = new HelpSection();
        section.setCategory(category);
        section.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        section.setGroups(new ArrayList<>());
        section.setImage("Image");
        section.setId("Key");
        section.setSection("HelpSection");
        section.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<HelpSection> ofResult = Optional.of(section);
        when(sectionRepository.findByCategory_IdAndId(Mockito.any(), Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<List<HelpGroupResponse>> actualFetchGroupsResult = helpImplementation.fetchGroups(
                "Key", "Key"
        );

        // Assert
        verify(sectionRepository).findByCategory_IdAndId(Mockito.any(), Mockito.any());
        assertEquals("Groups fetched", actualFetchGroupsResult.getMessage());
        assertEquals(200, actualFetchGroupsResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFetchGroupsResult.getStatus());
        assertTrue(actualFetchGroupsResult.getData().isEmpty());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchGroups(String, String)}
     */
    @Test
    void testFetchGroups2() {
        // Arrange
        Optional<HelpSection> emptyResult = Optional.empty();
        when(sectionRepository.findByCategory_IdAndId(Mockito.any(), Mockito.any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.fetchGroups("Key", "Key"));
        verify(sectionRepository).findByCategory_IdAndId(Mockito.any(), Mockito.any());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchGroups(String, String)}
     */
    @Test
    void testFetchGroups3() {
        // Arrange
        when(sectionRepository.findById(Mockito.any())).thenThrow(new HelpException("An error occurred"));

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.fetchGroups("Key", "Key"));
        verify(sectionRepository).findByCategory_IdAndId(Mockito.any(), Mockito.any());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchHelp(UUID)}
     */
    @Test
    void testFetchHelp() {
        // Arrange
        HelpCategory category = new HelpCategory();
        category.setCategory("HelpCategory");
        category.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        category.setImage("Image");
        category.setId("Key");
        category.setSections(new ArrayList<>());
        category.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        HelpSection section = new HelpSection();
        section.setCategory(category);
        section.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        section.setGroups(new ArrayList<>());
        section.setImage("Image");
        section.setId("Key");
        section.setSection("HelpSection");
        section.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        HelpGroup group = new HelpGroup();
        group.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        group.setGroup("HelpGroup");
        group.setHelps(new ArrayList<>());
        group.setId("Key");
        group.setSection(section);
        group.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        Help help = new Help();
        help.setAnswer("Answer");
        help.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        help.setGroup(group);
        UUID id = UUID.randomUUID();
        help.setId(id);
        help.setQuestion("Question");
        help.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<Help> ofResult = Optional.of(help);
        when(helpRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ApiResponse<HelpResponse> actualFetchHelpResult = helpImplementation.fetchHelp(UUID.randomUUID());

        // Assert
        verify(helpRepository).findById(Mockito.any());
        HelpResponse data = actualFetchHelpResult.getData();
        assertEquals("Answer", data.getAnswer());
        assertEquals("Help fetched", actualFetchHelpResult.getMessage());
        assertEquals("Question", data.getQuestion());
        assertEquals(200, actualFetchHelpResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualFetchHelpResult.getStatus());
        assertSame(id, data.getId());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchHelp(UUID)}
     */
    @Test
    void testFetchHelp2() {
        // Arrange
        Optional<Help> emptyResult = Optional.empty();
        when(helpRepository.findById(Mockito.any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.fetchHelp(UUID.randomUUID()));
        verify(helpRepository).findById(Mockito.any());
    }

    /**
     * Method under test: {@link HelpImplementation#fetchHelp(UUID)}
     */
    @Test
    void testFetchHelp3() {
        // Arrange
        when(helpRepository.findById(Mockito.any())).thenThrow(new HelpException("An error occurred"));

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.fetchHelp(UUID.randomUUID()));
        verify(helpRepository).findById(Mockito.any());
    }

    /**
     * Method under test: {@link HelpImplementation#ask(HelpAskRequest)}
     */
    @Test
    void testAsk() {
        // Arrange
        HelpAsk ask = new HelpAsk();
        ask.setComment("Comment");
        ask.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ask.setEmailAddress("42 Main St");
        ask.setFullName("Dr Jane Doe");
        ask.setId(UUID.randomUUID());
        ask.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        when(askRepository.save(Mockito.any())).thenReturn(ask);

        HelpAskRequest request = new HelpAskRequest();
        request.setComment("Comment");
        request.setEmailAddress("42 Main St");
        request.setFullName("Dr Jane Doe");

        // Act
        ApiResponse<String> actualAskResult = helpImplementation.ask(request);

        // Assert
        verify(askRepository).save(Mockito.any());
        assertEquals("Query submitted", actualAskResult.getMessage());
        assertNull(actualAskResult.getData());
        assertEquals(200, actualAskResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualAskResult.getStatus());
    }

    /**
     * Method under test: {@link HelpImplementation#ask(HelpAskRequest)}
     */
    @Test
    void testAsk2() {
        // Arrange
        when(askRepository.save(Mockito.any())).thenThrow(new HelpException("An error occurred"));

        HelpAskRequest request = new HelpAskRequest();
        request.setComment("Comment");
        request.setEmailAddress("42 Main St");
        request.setFullName("Dr Jane Doe");

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.ask(request));
        verify(askRepository).save(Mockito.any());
    }

    /**
     * Method under test: {@link HelpImplementation#search(String)}
     */
    @Test
    void testSearch() {
        // Arrange
        when(helpRepository.search(Mockito.any())).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<HelpSearchResponse>> actualSearchResult = helpImplementation.search("Query");

        // Assert
        verify(helpRepository).search(Mockito.any());
        assertEquals("Query found", actualSearchResult.getMessage());
        assertEquals(200, actualSearchResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualSearchResult.getStatus());
        assertTrue(actualSearchResult.getData().isEmpty());
    }

    /**
     * Method under test: {@link HelpImplementation#search(String)}
     */
    @Test
    void testSearch2() {
        // Arrange
        when(helpRepository.search(Mockito.any())).thenThrow(new HelpException("An error occurred"));

        // Act and Assert
        assertThrows(HelpException.class, () -> helpImplementation.search("Query"));
        verify(helpRepository).search(Mockito.any());
    }
}
