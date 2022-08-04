package de.abstractolotl.azplace.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String origin = request.getHeader("Origin") != null ? request.getHeader("Origin") : request.getHeader(HttpHeaders.HOST);

        if(!request.getRequestURI().contains("/swagger-ui")) {
            response.addHeader("Access-Control-Allow-Origin", "https://" + origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, DELETE");
            response.addHeader("Access-Control-Max-Age", "86400");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
