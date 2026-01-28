package jyhs.trail.application.usecases.auth;

import jyhs.trail.application.dto.LoginResponse;
import jyhs.trail.domain.model.RefreshToken;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.UserRepository;
import jyhs.trail.domain.service.PasswordHasher;
import jyhs.trail.domain.service.TokenService;
import org.springframework.stereotype.Component;

@Component
public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService; // <-- Inyectamos el puerto
    private final RefreshTokenUseCase refreshTokenUseCase; // 🛡️ Inyectamos el nuevo caso de uso

    public LoginUserUseCase(UserRepository userRepository,
                            PasswordHasher passwordHasher,
                            TokenService tokenService,
                            RefreshTokenUseCase refreshTokenUseCase) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    public LoginResponse execute(String username, String rawPassword) {
        // 1. Validar Usuario
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // 2. Validar Password
        if (!passwordHasher.matches(rawPassword, user.password())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3. Generar Access Token (el de siempre, vida corta)
        String accessToken = tokenService.generateToken(user);

        // 4. Generar y PERSISTIR el Refresh Token (vida larga)
        // El RefreshTokenUseCase se encarga de UUID, expiración y guardado en DB
        RefreshToken refreshTokenObj = refreshTokenUseCase.create(user.id());

        // 5. Devolvemos ambos para que el Controlador los ponga en cookies
        return new LoginResponse(user.username(), accessToken, refreshTokenObj.token());
    }
}