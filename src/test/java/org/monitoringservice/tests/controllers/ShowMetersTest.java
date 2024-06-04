package org.monitoringservice.tests.controllers;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.monitoringservice.in.controllers.ShowMetersController;
import org.monitoringservice.services.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ShowMetersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ShowMetersTest implements ControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MeterService meterService;

    @Test
    @DisplayName("Пользователь отправил запрос GET c адресом api/showMeters")
    public void getMetersForUser() throws Exception{
        when(meterService.getUserMeters(12)).thenReturn(getTestList());

        MockHttpSession testSession = getTestSessionForUser();

        mockMvc.perform(get("/api/showMeters").session(testSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Администратор отправил запрос GET c адресом api/showMeters")
    public void getMetersForAdmin() throws Exception{
        when(meterService.getMeterTypes()).thenReturn(getTestList());

        MockHttpSession testSession = getTestSessionForAdmin();

        mockMvc.perform(get("/api/showMeters").session(testSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
