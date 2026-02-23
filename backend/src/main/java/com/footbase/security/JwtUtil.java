package com.footbase.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.gizli-anahtar}")
    private String gizliAnahtar;

    @Value("${jwt.gecerlilik-suresi}")
    private Long gecerlilikSuresi;

    private SecretKey getSigningKey() {
        byte[] keyBytes = gizliAnahtar.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getKullaniciEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Long getKullaniciIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object kullaniciIdObj = claims.get("kullaniciId");
        if (kullaniciIdObj instanceof Integer) {
            return ((Integer) kullaniciIdObj).longValue();
        } else if (kullaniciIdObj instanceof Long) {
            return (Long) kullaniciIdObj;
        }
        return null;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String generateToken(String email, Long kullaniciId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("kullaniciId", kullaniciId);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + gecerlilikSuresi))
                .signWith(getSigningKey())
                .compact();
    }
}
