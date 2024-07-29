package com.hb.concert.support.config.util;


import com.hb.concert.domain.exception.CustomException.QueueTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    private final String SECRET_KEY;

    public JwtUtil(@Value("${jwt.secretkey}") String secretKey) {
        this.SECRET_KEY = secretKey;
    }

    public String generateToken(UUID userId, int position, String concertDetailId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("position", position);
        claims.put("concertDetailId", concertDetailId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10분 유효
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Map<String, Object> validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new QueueTokenException(QueueTokenException.TOKEN_UNAUTHORIZED);
        }

    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return UUID.fromString(claims.getSubject());
    }

    public int getPositionFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return Integer.parseInt(claims.get("position").toString());
        } catch (Exception e) {
            throw new QueueTokenException(QueueTokenException.TOKEN_UNAUTHORIZED);
        }
    }
}