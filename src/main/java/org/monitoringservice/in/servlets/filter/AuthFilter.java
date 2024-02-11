package org.monitoringservice.in.servlets.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Класс веб фильтр. Используется для авторизации пользователя и проверки его сессии.
 */
@WebFilter("/api/*")
public class AuthFilter implements Filter {
    /**
     * Метод инициализации фильтра.
     *
     * @param filterConfig конфигурация
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Метод, обеспечивающий фильтрацию. Выполняет проверку сессии пользователя на авторизацию.
     *
     * @param servletRequest  запрос к сервлету
     * @param servletResponse ответ от сервлета
     * @param filterChain     цепь фильтров
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        HttpSession session = req.getSession();

        String login = (String) session.getAttribute("login");
        Integer id = (Integer) session.getAttribute("id");

        if (login != null && id != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Метод разрушения фильтра.
     */
    @Override
    public void destroy() {
    }
}