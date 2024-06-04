package org.monitoringservice.in.controllers.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Класс фильтра, для авторизации. Все запросы, в пути которых встречается "/api/..." будут проходить через этот фильтр.
 */
@Component
public class AuthenticationFilter implements Filter {
    /**
     * Метод, который и производит "фильтрацию".
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        HttpSession session = req.getSession();

        String login;
        int id;
        try {
            login = (String) session.getAttribute("login");
            id = Integer.parseInt(req.getSession().getAttribute("id").toString());
        } catch (NumberFormatException | NullPointerException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (login != null && id != -1) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
