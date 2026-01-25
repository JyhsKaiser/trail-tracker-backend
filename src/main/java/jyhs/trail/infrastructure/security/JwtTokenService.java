package jyhs.trail.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.service.TokenService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenService implements TokenService {

    private final String SECRET_KEY = "tu_clave_secreta_super_larga_para_la_uaem_trail_tracker";
    private final long EXPIRATION_TIME = 86400000; // 24 horas

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.username())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
