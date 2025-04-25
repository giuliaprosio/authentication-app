package com.springapplication.userapp.configuration.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ForwardingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final String forwardTo;

    public ForwardingAuthenticationEntryPoint(){
        this.forwardTo = "/index.html";
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        request.getRequestDispatcher(forwardTo).forward(request, response);
    }
}
