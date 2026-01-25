package jyhs.trail.application.usecases.run;

import jyhs.trail.domain.model.Run;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.RunRepository;
import jyhs.trail.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class SaveRunUseCase {
    private final RunRepository runRepository;
    private final UserRepository userRepository;

    public SaveRunUseCase(RunRepository runRepository, UserRepository userRepository) {
        this.runRepository = runRepository;
        this.userRepository = userRepository;
    }

    public Run execute(Run run, String username) {
        // 1. Buscamos al usuario real por su nombre (extraído del token)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Creamos la carrera asociada a ese ID
        Run runWithUser = new Run(
                null,
                run.name(),
                run.distanceKm(),
                run.elevationGain(),
                run.date(),
                user.id()
        );

        return runRepository.save(runWithUser);
    }
}