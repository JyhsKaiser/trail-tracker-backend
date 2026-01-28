package jyhs.trail.application.usecases.user;

import jyhs.trail.application.dto.UserResponseDTO;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class GetCurrentUserUseCase {
    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO execute(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UserResponseDTO(user.id(), user.username(), user.email());
    }
}