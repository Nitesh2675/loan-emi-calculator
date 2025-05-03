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

@Component
public class JwtUtility {

    @Autowired
    private UserRepository userRepository;

    // Use a strong key and consistent setup
    private static final String SECRET_KEY = "fd123gbj23hu12h3k123j123fd123gbj23hu12h3k123j123"; // at least 256 bits
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) throws JwtException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token, String userEmail) throws JwtException {
        String email = extractEmail(token); // throws JwtException if invalid/expired
        User user = userRepository.findByEmail(email).orElse(null);

        boolean isTokenExpired = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());

        return (user != null && email.equals(userEmail) && !isTokenExpired);
    }
}
