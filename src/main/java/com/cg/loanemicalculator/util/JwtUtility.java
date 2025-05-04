package com.cg.loanemicalculator.util;

import com.cg.loanemicalculator.model.User;
import com.cg.loanemicalculator.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class JwtUtility {

    private static final String SECRET_KEY = "fd123gbj23hu12h3k123j123fd123gbj23hu12h3k123j123";
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // In-memory store for valid tokens. Replace with Redis for production.
    private final Map<String, String> validTokens = new ConcurrentHashMap<>();

    public String generateToken(String email) {
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        validTokens.put(email, token); // Overwrite old token
        return token;
    }

    public String extractEmail(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String userEmail) throws JwtException {
        String email = extractEmail(token);
        boolean isTokenExpired = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());

        // Check if token matches the current valid token
        return (email.equals(userEmail) && !isTokenExpired && token.equals(validTokens.get(email)));
    }

    public void invalidateToken(String email) {
        validTokens.remove(email);
    }
}
