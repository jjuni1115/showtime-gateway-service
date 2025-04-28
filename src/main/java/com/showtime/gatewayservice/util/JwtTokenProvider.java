package com.showtime.gatewayservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {


    private final SecretKey key;
    private final long expirationTime;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret, @Value("${spring.jwt.expiration}") long expirationTime) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }



    public Boolean validateToken(String token) {

            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;


    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key)
                .build().parseSignedClaims(token).getPayload();
    }

    public String getUserEmail(String token){
        return this.extractAllClaims(token).get("userEmail").toString();
    }

    public String getUserId(String token){
        return this.extractAllClaims(token).get("userId").toString();
    }


}
