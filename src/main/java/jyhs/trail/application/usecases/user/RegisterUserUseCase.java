package jyhs.trail.application.usecases.user;

import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.UserRepository;
import jyhs.trail.domain.service.PasswordHasher;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(User user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }

        // 🛡️ Transformamos la contraseña de texto plano a Hash
        String encryptedPassword = passwordHasher.encode(user.password());

        User securedUser = new User(
                null,
                user.username(),
                user.email(),
                encryptedPassword
        );

        return userRepository.save(securedUser);
    }
}