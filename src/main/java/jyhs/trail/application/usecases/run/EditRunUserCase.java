package jyhs.trail.application.usecases.run;

import jakarta.transaction.Transactional;
import jyhs.trail.application.usecases.auth.GetAuthenticatedUserUseCase;
import jyhs.trail.domain.model.Run;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.RunRepository;
import jyhs.trail.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditRunUserCase {
    private final RunRepository runRepository; // Repositorio del dominio
    private final GetAuthenticatedUserUseCase getAuthenticatedUser;

    // application/usecases/run/EditRunUseCase.java
    @Transactional
    public Run execute(Long id, Run request) {
        // 1. Buscar el actual
        Run existingRun = runRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada con ID: " + id));

        // 2. Validar propiedad (GetAuthenticatedUserUseCase)
        // ... (tu lógica de validación de usuario)

        // 3. Crear el nuevo Record (Inmutabilidad)
        // Importante: Si request.name() es null, usamos existingRun.name()
        Run updatedRun = new Run(
                existingRun.id(), // Usamos el ID del que encontramos en DB
                request.name() != null ? request.name() : existingRun.name(),
                request.distanceKm() > 0 ? request.distanceKm() : existingRun.distanceKm(),
                request.elevationGain() >= 0 ? request.elevationGain() : existingRun.elevationGain(),
                request.date() != null ? request.date() : existingRun.date(),
                existingRun.userId() // Mantenemos al dueño original
        );

        // 4. PERSISTIR Y RETORNAR
        return runRepository.save(updatedRun);
    }
}
