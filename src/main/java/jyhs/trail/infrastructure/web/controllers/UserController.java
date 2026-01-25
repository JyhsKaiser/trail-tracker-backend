package jyhs.trail.infrastructure.web.controllers;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jyhs.trail.application.dto.LoginResponse;
import jyhs.trail.application.usecases.user.GetCurrentUserUseCase;
import jyhs.trail.application.usecases.user.LoginUserUseCase;
import jyhs.trail.application.usecases.user.RegisterUserUseCase;
import jyhs.trail.domain.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Permite la conexión con Angular
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase, GetCurrentUserUseCase getCurrentUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        // 🛡️ Extraemos el nombre del usuario que el Filtro JWT validó previamente
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = getCurrentUserUseCase.execute(username);
        System.out.println("User: " + user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Creamos una cookie con el mismo nombre pero expirada
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 🚀 Esto le indica al navegador que la borre

        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response // Inyectamos la respuesta de Servlet
    ) {
        try {
            LoginResponse loginResponse = loginUserUseCase.execute(
                    loginRequest.username(),
                    loginRequest.password()
            );

            // Creamos la Cookie
            Cookie cookie = new Cookie("token", loginResponse.token());
            cookie.setHttpOnly(true);   // 🛡️ Clave: JavaScript no puede leerla
            cookie.setSecure(false);    // Cambiar a TRUE cuando uses HTTPS
            cookie.setPath("/");        // Disponible para toda la app
            cookie.setMaxAge(86400);    // 24 horas de vida

            response.addCookie(cookie);

            // Devolvemos solo el nombre de usuario (el token ya va en la cabecera Set-Cookie)
            return ResponseEntity.ok(new LoginResponse(loginResponse.username(), null));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    // Un pequeño Record para recibir los datos del JSON
    public record LoginRequest(String username, String password) {}
}