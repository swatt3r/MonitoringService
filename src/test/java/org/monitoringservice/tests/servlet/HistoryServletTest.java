package org.monitoringservice.tests.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.entities.Role;
import org.monitoringservice.in.servlets.HistoryServlet;
import org.monitoringservice.services.MeterService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryServletTest {
    @Mock
    private MeterService meterService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    private HistoryServlet historyServlet;

    @Test
    @DisplayName("Тест получения истории показаний")
    public void successHistoryTest() throws Exception {
        HttpSession session = Mockito.mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);

        when(session.getAttribute("role")).thenReturn(Role.USER);
        when(session.getAttribute("id")).thenReturn(12);

        LinkedList<String> result = new LinkedList<>();
        result.add("res1");
        result.add("res2");

        when(meterService.getUserHistory(12)).thenReturn(result);

        ServletOutputStream outputStream = Mockito.mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenReturn(outputStream);

        historyServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
