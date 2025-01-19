package com.serch.server.domains.schedule;

import com.serch.server.domains.schedule.controllers.ScheduleController;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
//                                "{\"status\":\"BAD_REQUEST\",\"code\":400,\"message\":\"Not all who wander are lost\",\"response\":null}"));
//    }
}
