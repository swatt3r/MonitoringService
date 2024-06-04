package org.monitoringservice.config;

import org.monitoringservice.in.controllers.filters.AuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthFilterConfig {
    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authFilter(){
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(authenticationFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setName("authenticationFilter");
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean(name = "authenticationFilter")
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }
}
