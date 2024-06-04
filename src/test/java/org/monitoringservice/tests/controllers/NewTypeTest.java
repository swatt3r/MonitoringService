package org.monitoringservice.tests.controllers;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.monitoringservice.in.controllers.NewTypeController;
import org.monitoringservice.services.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@WebMvcTest(NewTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NewTypeTest implements ControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MeterService meterService;

    @Test
    @DisplayName("Пользователь отправил запрос POST c адресом api/newType")
    public void postNewTypeForUser() throws Exception{
        MockHttpSession testSession = getTestSessionForUser();

        String requestBody = "{\"type\":\"NewType\"}";

        mockMvc.perform(post("/api/newType").session(testSession)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Администратор отправил запрос POST c адресом api/newType")
    public void postNewTypeForAdmin() throws Exception{
        MockHttpSession testSession = getTestSessionForAdmin();

        String requestBody = "{\"type\":\"NewType\"}";

        mockMvc.perform(post("/api/newType").session(testSession)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}
