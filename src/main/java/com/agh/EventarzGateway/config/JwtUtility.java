package com.agh.EventarzGateway.config;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class JwtUtility {

    private final String jwtSecret = "eventarzJwtSecret";
    private final String jwtIssuer = "eventarz.agh.com";

    public String generateAccessToken(User user) {
        String username = user.getUsername();
        return generateAccessToken(username);
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(format("%s", username))
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 1 day
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Jwt decode(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
    }
}
