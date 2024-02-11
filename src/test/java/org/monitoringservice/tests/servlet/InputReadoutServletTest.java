package org.monitoringservice.tests.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.dto.NewReadoutDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.in.servlets.InputReadoutServlet;
import org.monitoringservice.services.MeterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.StringReader;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InputReadoutServletTest {
    @Mock
    private MeterService meterService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    private InputReadoutServlet inputReadoutServlet;

    @Test
    @DisplayName("Тест успешной подачи показаний")
    public void successReadoutTest() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader("json"));

        NewReadoutDTO readoutDTO = new NewReadoutDTO();
        readoutDTO.setReadout(55);
        readoutDTO.setType("Heat");

        when(req.getReader()).thenReturn(reader);
        when(objectMapper.readValue("json", NewReadoutDTO.class)).thenReturn(readoutDTO);

        HttpSession session = Mockito.mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);

        when(session.getAttribute("role")).thenReturn(Role.USER);
        when(session.getAttribute("id")).thenReturn(12);

        inputReadoutServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
