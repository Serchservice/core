package com.serch.server.services.schedule;

import static org.mockito.Mockito.when;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.schedule.services.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {ScheduleController.class})
@ExtendWith(SpringExtension.class)
class ScheduleControllerTest {
//    @Autowired
//    private ScheduleController scheduleController;
//
//    @MockBean
//    private ScheduleService scheduleService;
//
//    /**
//     * Method under test: {@link ScheduleController#accept(String)}
//     */
//    @Test
//    void testAccept() throws Exception {
//        // Arrange
//        when(scheduleService.accept(Mockito.<String>any())).thenReturn(new ApiResponse<>("Not all who wander are lost"));
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/schedule/accept/{id}", "42");
//
//        // Act
//        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(scheduleController)
//                .build()
//                .perform(requestBuilder);
//
//        // Assert
//        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
//                .andExpect(MockMvcResultMatchers.content()
//                        .string(
//                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"data\":null}"));
//    }
}
