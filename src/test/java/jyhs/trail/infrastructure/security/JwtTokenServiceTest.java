package jyhs.trail.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jyhs.trail.domain.model.User;
import jyhs.trail.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    // Valores de prueba (simulan tu application.properties)
    private final String TEST_SECRET = "mi_clave_super_secreta_de_64_caracteres_minimo_para_seguridad_hs256";
    private final long ACCESS_TOKEN_TIME = 900000L; // 15 min
    private final long REFRESH_TOKEN_TIME = 604800000L; // 7 días

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService();

        // "Inyectamos" los @Value manualmente
        ReflectionTestUtils.setField(jwtTokenService, "SECRET_KEY", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenService, "jwtExpiration", ACCESS_TOKEN_TIME);
        ReflectionTestUtils.setField(jwtTokenService, "refreshExpiration", REFRESH_TOKEN_TIME);
    }

    @Test
    @DisplayName("Debería generar un Access Token que expire en 15 minutos")
    void testAccessTokenExpiration() {
        // Arrange
        User user = new User(1L, "a", "a", "a");

        // Act
        String token = jwtTokenService.generateToken(user);

        // Assert: Parseamos el token para ver qué hay dentro
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        long durationMillis = expiration.getTime() - issuedAt.getTime();

        assertEquals(ACCESS_TOKEN_TIME, durationMillis, "La duración del token no coincide con los 15 min configurados");

        System.out.println(durationMillis/1000/60);
    }

    @Test
    @DisplayName("Debería generar un Refresh Token que expire en 7 días")
    void testRefreshTokenExpiration() {
        // Act
        String token = jwtTokenService.generateRefreshToken(new User(1L, "a", "a", "a"));

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long durationMillis = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        assertEquals(REFRESH_TOKEN_TIME, durationMillis);
        System.out.println(durationMillis/1000/60/60/24);
    }
}