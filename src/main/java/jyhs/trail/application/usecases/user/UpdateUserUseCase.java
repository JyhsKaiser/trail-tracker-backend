package jyhs.trail.application.usecases.user;

import jakarta.transaction.Transactional;
import jyhs.trail.application.usecases.auth.GetAuthenticatedUserUseCase;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final GetAuthenticatedUserUseCase getAuthenticatedUser;

    @Transactional
    public User execute(User request) {
        // 1. Obtener el usuario autenticado (Record de dominio)
        User currentUser = getAuthenticatedUser.execute();

        // 2. Crear una nueva instancia del Record (PATCH parcial)
        // Solo actualizamos username y email si vienen en el request
        User updatedUser = new User(
                currentUser.id(), // Mantenemos el ID original
                currentUser.username(), // 👈 Identidad bloqueada
                request.email() != null ? request.email() : currentUser.email(),
                currentUser.password() // Mantenemos el password actual intacto
        );

        // 3. Persistir los cambios
        return userRepository.save(updatedUser);
    }
}