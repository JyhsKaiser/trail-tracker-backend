package jyhs.trail.application.usecases.user;

import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class GetCurrentUserUseCase {
    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}