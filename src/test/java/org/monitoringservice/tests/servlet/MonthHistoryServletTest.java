package org.monitoringservice.tests.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.dto.MonthSearchDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.in.servlets.MonthHistoryServlet;
import org.monitoringservice.services.MeterService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MonthHistoryServletTest {
    @Mock
    private MeterService meterService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    private MonthHistoryServlet monthHistoryServlet;

    @Test
    @DisplayName("Тест получения истории показаний за месяц")
    public void monthHistoryTest() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader("json"));
        when(req.getReader()).thenReturn(reader);

        LinkedList<String> result = new LinkedList<>();
        result.add("res1");
        result.add("res2");
        when(meterService.getUserMonthHistory(12, 11)).thenReturn(result);

        MonthSearchDTO monthDTO = new MonthSearchDTO();
        monthDTO.setMonth(11);
        when(objectMapper.readValue("json", MonthSearchDTO.class)).thenReturn(monthDTO);

        HttpSession session = Mockito.mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn(Role.USER);
        when(session.getAttribute("id")).thenReturn(12);

        ServletOutputStream outputStream = Mockito.mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenReturn(outputStream);

        monthHistoryServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
