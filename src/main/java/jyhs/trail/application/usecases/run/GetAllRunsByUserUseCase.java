package jyhs.trail.application.usecases.run;

import jyhs.trail.domain.model.Run;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.RunRepository;
import jyhs.trail.domain.repository.UserRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GetAllRunsByUserUseCase {
    private final RunRepository runRepository;
    private final UserRepository userRepository;

    public GetAllRunsByUserUseCase(RunRepository runRepository, UserRepository userRepository) {
        this.runRepository = runRepository;
        this.userRepository = userRepository;
    }

    public List<Run> execute(String username) {
        // 1. Validamos que el usuario exista en el sistema
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Usamos el ID del usuario para filtrar las carreras en el repositorio
        // Solo el dueño puede ver sus datos
        return runRepository.findByUserId(user.id());
    }
}