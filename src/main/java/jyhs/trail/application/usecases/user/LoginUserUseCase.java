package jyhs.trail.application.usecases.user;

import jyhs.trail.application.dto.LoginResponse;
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

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public LoginResponse execute(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordHasher.matches(rawPassword, user.password())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Generamos el token usando el puerto del dominio
        String token = tokenService.generateToken(user);

        return new LoginResponse(user.username(), token);
    }
}

