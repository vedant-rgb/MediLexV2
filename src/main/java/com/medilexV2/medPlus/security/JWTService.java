package com.medilexV2.medPlus.security;

import com.medilexV2.medPlus.entity.Medical;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Medical medical){
        return Jwts.builder()
                .subject(medical.getUsername())
                .claim("role", medical.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +1000*60*60))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(Medical medical){
        return Jwts.builder()
                .subject(medical.getUsername())
                .claim("role", medical.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +1000L*60*60*24*30*6))
                .signWith(getSecretKey())
                .compact();
    }

    public String getEmailFromToken(String token){
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        }
        catch (JwtException ex){
            throw new JwtException(ex.getLocalizedMessage());
        }
    }

}
