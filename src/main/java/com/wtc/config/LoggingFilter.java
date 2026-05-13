package com.wtc.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Gera um ID único para esta chamada (Correlation ID)
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("correlationId", correlationId); // Coloca no log do Spring

        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("[REQ] ID: " + correlationId + " | Metodo: " + req.getMethod() + " | URL: " + req.getRequestURI());

        chain.doFilter(request, response);
        MDC.clear();
    }
}