package jyhs.trail.application.usecases.user;

import jakarta.transaction.Transactional;
import jyhs.trail.application.usecases.auth.GetAuthenticatedUserUseCase;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.UserRepository;
import jyhs.trail.infrastructure.web.dto.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangePasswordUseCase {
    private final UserRepository userRepository;
    private final GetAuthenticatedUserUseCase getAuthenticatedUser;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void execute(PasswordChangeRequest request) {
        // 1. Obtener usuario autenticado (Record de dominio)
        User currentUser = getAuthenticatedUser.execute();

        // 2. Verificar que la contraseña actual coincida con la de la BD
        if (!passwordEncoder.matches(request.oldPassword(), currentUser.password())) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }

        // 3. Crear nuevo Record con la contraseña HASHEADA
        User updatedUser = new User(
                currentUser.id(),
                currentUser.username(),
                currentUser.email(),
                passwordEncoder.encode(request.newPassword())
        );

        // 4. Persistir (El JpaAdapter ya tiene el setId para hacer UPDATE)
        userRepository.save(updatedUser);
    }
}