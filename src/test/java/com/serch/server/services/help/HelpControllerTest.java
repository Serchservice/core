package com.serch.server.services.help;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.help.models.Help;
import com.serch.server.services.help.models.HelpCategory;
import com.serch.server.services.help.models.HelpGroup;
import com.serch.server.services.help.models.HelpSection;
import com.serch.server.services.help.repositories.*;
import com.serch.server.services.help.requests.HelpAskRequest;
import com.serch.server.services.help.response.HelpGroupResponse;
import com.serch.server.services.help.response.HelpResponse;
import com.serch.server.services.help.response.HelpSectionResponse;
import com.serch.server.services.help.services.HelpImplementation;
import com.serch.server.services.help.services.HelpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {HelpController.class})
@ExtendWith(SpringExtension.class)
class HelpControllerTest {
    @Autowired
    private HelpController helpController;

    @MockBean
    private HelpService helpService;

    /**
     * Method under test: {@link HelpController#fetchCategories()}
     */
    @Test
    void testFetchCategories() throws Exception {
        // Arrange
        when(helpService.fetchCategories()).thenReturn(new ApiResponse<>("Not all who wander are lost"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/company/help/categories");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(helpController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"data\":null}"));
    }

    /**
     * Method under test: {@link HelpController#fetchSections(String)}
     */
    @Test
    void testFetchSections() {
        // Arrange
        HelpCategory category = new HelpCategory();
        category.setCategory("HelpCategory");
        category.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        category.setImage("Image");
        category.setKey("Key");
        category.setSections(new ArrayList<>());
        category.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<HelpCategory> ofResult = Optional.of(category);
        HelpCategoryRepository categoryRepository = mock(HelpCategoryRepository.class);
        when(categoryRepository.findById(Mockito.any())).thenReturn(ofResult);

        // Act
        ResponseEntity<ApiResponse<List<HelpSectionResponse>>> actualFetchSectionsResult = (new HelpController(
                new HelpImplementation(
                        categoryRepository, mock(HelpSectionRepository.class),
                        mock(HelpRepository.class), mock(HelpGroupRepository.class),
                        mock(HelpAskRepository.class)
                )))
                .fetchSections("Key");

        // Assert
        verify(categoryRepository).findById(Mockito.any());
        ApiResponse<List<HelpSectionResponse>> body = actualFetchSectionsResult.getBody();
        assert body != null;
        assertEquals("Sections fetched", body.getMessage());
        assertEquals(200, body.getCode().intValue());
        assertEquals(200, actualFetchSectionsResult.getStatusCode().value());
        assertEquals(HttpStatus.OK, body.getStatus());
        assertTrue(body.getData().isEmpty());
        assertTrue(actualFetchSectionsResult.hasBody());
        assertTrue(actualFetchSectionsResult.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link HelpController#fetchGroups(String, String)}
     */
    @Test
    void testFetchGroups() {
        // Arrange
        HelpCategory category = new HelpCategory();
        category.setCategory("HelpCategory");
        category.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        category.setImage("Image");
        category.setKey("Key");
        category.setSections(new ArrayList<>());
        category.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        HelpSection section = new HelpSection();
        section.setCategory(category);
        section.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        section.setGroups(new ArrayList<>());
        section.setImage("Image");
        section.setKey("Key");
        section.setSection("HelpSection");
        section.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<HelpSection> ofResult = Optional.of(section);
        HelpSectionRepository sectionRepository = mock(HelpSectionRepository.class);
        when(sectionRepository.findByCategory_KeyAndKey(Mockito.any(), Mockito.any())).thenReturn(ofResult);

        // Act
        ResponseEntity<ApiResponse<List<HelpGroupResponse>>> actualFetchGroupsResult = (new HelpController(
                new HelpImplementation(
                        mock(HelpCategoryRepository.class), sectionRepository,
                        mock(HelpRepository.class), mock(HelpGroupRepository.class),
                        mock(HelpAskRepository.class)
                )))
                .fetchGroups("Key", "Key");

        // Assert
        verify(sectionRepository).findByCategory_KeyAndKey(Mockito.any(), Mockito.any());
        ApiResponse<List<HelpGroupResponse>> body = actualFetchGroupsResult.getBody();
        assert body != null;
        assertEquals("Groups fetched", body.getMessage());
        assertEquals(200, body.getCode().intValue());
        assertEquals(200, actualFetchGroupsResult.getStatusCode().value());
        assertEquals(HttpStatus.OK, body.getStatus());
        assertTrue(body.getData().isEmpty());
        assertTrue(actualFetchGroupsResult.hasBody());
        assertTrue(actualFetchGroupsResult.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link HelpController#fetchHelp(UUID)}
     */
    @Test
    void testFetchHelp() {
        // Arrange
        HelpCategory category = new HelpCategory();
        category.setCategory("HelpCategory");
        category.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        category.setImage("Image");
        category.setKey("Key");
        category.setSections(new ArrayList<>());
        category.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        HelpSection section = new HelpSection();
        section.setCategory(category);
        section.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        section.setGroups(new ArrayList<>());
        section.setImage("Image");
        section.setKey("Key");
        section.setSection("HelpSection");
        section.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        HelpGroup group = new HelpGroup();
        group.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        group.setGroup("HelpGroup");
        group.setHelps(new ArrayList<>());
        group.setKey("Key");
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
        HelpRepository helpRepository = mock(HelpRepository.class);
        when(helpRepository.findById(Mockito.any())).thenReturn(ofResult);
        HelpController helpController = new HelpController(
                new HelpImplementation(
                        mock(HelpCategoryRepository.class), mock(HelpSectionRepository.class),
                        helpRepository, mock(HelpGroupRepository.class),
                        mock(HelpAskRepository.class)
                ));

        // Act
        ResponseEntity<ApiResponse<HelpResponse>> actualFetchHelpResult = helpController.fetchHelp(UUID.randomUUID());

        // Assert
        verify(helpRepository).findById(Mockito.any());
        ApiResponse<HelpResponse> body = actualFetchHelpResult.getBody();
        assert body != null;
        HelpResponse data = body.getData();
        assertEquals("Answer", data.getAnswer());
        assertEquals("Help fetched", body.getMessage());
        assertEquals("Question", data.getQuestion());
        assertEquals(200, body.getCode().intValue());
        assertEquals(200, actualFetchHelpResult.getStatusCode().value());
        assertEquals(HttpStatus.OK, body.getStatus());
        assertTrue(actualFetchHelpResult.hasBody());
        assertTrue(actualFetchHelpResult.getHeaders().isEmpty());
        assertSame(id, data.getId());
    }

    /**
     * Method under test: {@link HelpController#search(String)}
     */
    @Test
    void testSearch() throws Exception {
        // Arrange
        when(helpService.search(Mockito.any())).thenReturn(new ApiResponse<>("Not all who wander are lost"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/company/help/search").param("q", "foo");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(helpController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"data\":null}"));
    }

    /**
     * Method under test: {@link HelpController#ask(HelpAskRequest)}
     */
    @Test
    void testAsk() throws Exception {
        // Arrange
        when(helpService.ask(Mockito.any())).thenReturn(new ApiResponse<>("Not all who wander are lost"));

        HelpAskRequest askRequest = new HelpAskRequest();
        askRequest.setComment("Comment");
        askRequest.setEmailAddress("42 Main St");
        askRequest.setFullName("Dr Jane Doe");
        String content = (new ObjectMapper()).writeValueAsString(askRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/company/help/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(helpController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"data\":null}"));
    }
}
