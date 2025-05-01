//package com.cg.loanemicalculator.util;
//
//
//
//import com.cg.loanemicalculator.model.User;
//import com.cg.loanemicalculator.repository.UserRepository;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JwtUtility {
//
//    @Autowired
//    UserRepository userRespository;
//
//    private static final String SECRET_KEY="fd123gbj23hu12h3k123j123";
//    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // secure 256-bit key
//
//
//    public String generateToken(String email){
//        return Jwts.builder()
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis()+5*60*1000))
//                //.signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//
//
//    }
//
//    public String extractEmail(String token){
//        try{
//            System.out.println(token);
//            Claims claims=Jwts.parserBuilder()
//                    //.setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            System.out.println("getting email =>"+claims);
//            return claims.getSubject();
//        } catch (Exception e) {
//            return e.getMessage();
//        }
//    }
//
//    public boolean validateToken(String token,String userEmail){
//        final String email=extractEmail(token);
//        boolean isTokenPresent=true;
//        User user=userRespository.findByEmail(email).orElse(null);
//
//        if(user!=null && token == null){
//            isTokenPresent=false;
//        }
//        final boolean valid=Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody().getExpiration().before(new Date());
//        return (email.equals(userEmail) && !valid && isTokenPresent);
//    }
//}
//
