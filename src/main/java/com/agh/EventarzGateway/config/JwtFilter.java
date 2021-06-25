package com.agh.EventarzGateway.config;

import com.agh.EventarzGateway.model.dtos.ErrorDTO;
import com.agh.EventarzGateway.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.util.StringUtils.isEmpty;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }


        // Get jwt token and validate
        final String tokenString = header.split(" ")[1].trim();
        Jwt token = null;
        try {
            token = jwtUtility.decode(tokenString);
        } catch (JwtException e) {
            // Token invalid or expired
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ErrorDTO(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    request.getRequestURI(),
                    "Token invalid!"
            ).toJson());
            return;
        }

        Claims claims = (Claims) token.getBody();
        String username = claims.getSubject();
        User userDetails;
        try {
            userDetails = (User) authenticationService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ErrorDTO(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    request.getRequestURI(),
                    "Token invalid!"
            ).toJson());
            return;
        }

        if (!userDetails.isAccountNonLocked()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ErrorDTO(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    request.getRequestURI(),
                    "Account locked!"
            ).toJson());
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
