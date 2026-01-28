package jyhs.trail.application.usecases.auth;

import jyhs.trail.domain.model.RefreshToken;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.RefreshTokenRepository;
import jyhs.trail.domain.repository.UserRepository;
import jyhs.trail.domain.service.TokenService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService; // <-- Inyectamos el puerto

    // Definimos la duración aquí mismo para que no sea una variable "fantasma"
    // 7 días expresados en milisegundos
    private final long REFRESH_TOKEN_DURATION_MS = 1000L * 60 * 60 * 24 * 7;

    // Constructor para inyección de dependencias
    public RefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, TokenService tokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    // Añade este método a tu clase existente
    public Optional<String> verifyAndGenerateNewAccess(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(t -> !t.isExpired()) // Usamos el método que ya tiene tu Record
                .map(t -> tokenService.generateToken(t.user())); // Generamos nuevo Access Token
    }

    public RefreshToken create(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RefreshToken refreshToken = new RefreshToken(
                null,
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS),
                user
        );

        return refreshTokenRepository.save(refreshToken);
    }
}