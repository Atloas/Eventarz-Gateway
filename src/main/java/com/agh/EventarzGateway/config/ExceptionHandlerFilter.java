package com.agh.EventarzGateway.config;

import com.agh.EventarzGateway.model.dtos.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {

            ErrorDTO errorDTO = new ErrorDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request.getRequestURI(),
                    "Something went wrong!"
            );

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(errorDTO.toJson());
        }
    }
}