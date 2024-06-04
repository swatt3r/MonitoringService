package org.monitoringservice.tests.controllers;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.monitoringservice.in.controllers.MonthHistoryController;
import org.monitoringservice.services.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@WebMvcTest(MonthHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MonthHistoryTest implements ControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MeterService meterService;

    @Test
    @DisplayName("Пользователь отправил запрос POST c адресом api/month")
    public void postMonthHistoryForUser() throws Exception{
        when(meterService.getUserMonthHistory(12, 10)).thenReturn(getTestList());

        MockHttpSession testSession = getTestSessionForUser();

        String requestBody = "{\"month\":\"10\"}";

        mockMvc.perform(post("/api/month").session(testSession)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Администратор отправил запрос POST c адресом api/month")
    public void postMonthHistoryForAdmin() throws Exception{
        when(meterService.getMonthHistoryForAdmin("user", 10)).thenReturn(getTestList());

        MockHttpSession testSession = getTestSessionForAdmin();

        String requestBody = "{\"login\":\"user\", \"month\":\"10\"}";

        mockMvc.perform(post("/api/month").session(testSession)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
