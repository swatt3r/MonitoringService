package org.monitoringservice.tests.controllers;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.in.controllers.AuthenticationController;
import org.monitoringservice.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthTest implements ControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthenticationService authService;

    @Test
    @DisplayName("Пользователь отправил правильный запрос POST c адресом /login")
    public void postLoginWithoutErrors() throws Exception{
        UserDTO testUserDTO = new UserDTO();
        testUserDTO.setLogin("user");
        testUserDTO.setPassword("user");
        testUserDTO.setId(12);
        when(authService.login("user", "user")).thenReturn(testUserDTO);

        String request = "{\"login\":\"user\", \"password\":\"user\"}";

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Пользователь отправил неправильный запрос POST c адресом /login")
    public void postLoginWithErrors() throws Exception{
        String request = "{\"login\":\"\", \"password\":\"user\"}";

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Пользователь правильный отправил запрос POST c адресом /registration")
    public void postRegisterWithoutErrors() throws Exception{
        String request = "{\"login\":\"userTest\", " +
                    "\"password\":\"userTest\", " +
                    "\"city\":\"TestCity\", " +
                    "\"street\":\"TestStreet\", " +
                    "\"houseNumber\":\"12\", " +
                    "\"apartmentNumber\":\"13\"}";

        mockMvc.perform(post("/registration")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Пользователь отправил неправильный запрос POST c адресом /registration")
    public void postRegisterWithErrors() throws Exception{
        String request = "{\"login\":\"userTest\", " +
                "\"password\":\"userTest\", " +
                "\"city\":\"\", " +
                "\"street\":\"TestStreet\", " +
                "\"houseNumber\":\"-100\", " +
                "\"apartmentNumber\":\"-1\"}";

        mockMvc.perform(post("/registration")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Пользователь отправил запрос GET c адресом /api/logout")
    public void getLogout() throws Exception{
        mockMvc.perform(get("/api/logout"))
                .andExpect(status().isOk());
    }
}
