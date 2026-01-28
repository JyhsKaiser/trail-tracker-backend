package jyhs.trail.infrastructure.web.controllers;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jyhs.trail.application.dto.LoginResponse;
import jyhs.trail.application.usecases.user.ChangePasswordUseCase;
import jyhs.trail.application.usecases.user.GetCurrentUserUseCase;
import jyhs.trail.application.usecases.auth.LoginUserUseCase;
import jyhs.trail.application.usecases.auth.RegisterUserUseCase;
import jyhs.trail.application.usecases.user.UpdateUserUseCase;
import jyhs.trail.domain.model.User;
import jyhs.trail.infrastructure.web.dto.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
//@CrossOrigin(origins = "*") // Permite la conexión con Angular
@RequiredArgsConstructor
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            User createdUser = registerUserUseCase.execute(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Manejo básico de errores (ej. si el usuario ya existe)
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateMyProfile(@RequestBody User userRequest) {
        User updated = updateUserUseCase.execute(userRequest);
        return ResponseEntity.ok(updated);
    }
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        // Ejecuta la validación de contraseña actual y el hasheo de la nueva
        changePasswordUseCase.execute(request);
        return ResponseEntity.noContent().build(); // 204 No Content es ideal para actualizaciones de seguridad
    }

}