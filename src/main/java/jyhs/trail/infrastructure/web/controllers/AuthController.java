package jyhs.trail.infrastructure.web.controllers;

import jyhs.trail.application.dto.LoginResponse;
import jyhs.trail.application.usecases.auth.LoginUserUseCase;
import jyhs.trail.application.usecases.auth.RefreshTokenUseCase;
import jyhs.trail.application.usecases.auth.RegisterUserUseCase;
import jyhs.trail.application.usecases.user.GetCurrentUserUseCase;
import jyhs.trail.application.dto.UserResponseDTO;
import jyhs.trail.infrastructure.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUserUseCase loginUserUseCase;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;


    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        // 🛡️ Extraemos el nombre del usuario que el Filtro JWT validó previamente
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserResponseDTO userResponse = getCurrentUserUseCase.execute(username);

        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = loginUserUseCase.execute(loginRequest.username(), loginRequest.password());
//
            ResponseCookie accessCookie = createCookie("accessToken", response.accessToken(), "/", jwtTokenService.getJwtExpirationInSeconds());
            ResponseCookie refreshCookie = createCookie("refreshToken", response.refreshToken(), "/api/auth/refresh",jwtTokenService.getRefreshExpirationInSeconds());

            UserResponseDTO userResponse = getCurrentUserUseCase.execute(response.username());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue(name = "refreshToken", required = false) String token) {
        if (token == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // 1. Validar el token en la DB y que no haya expirado
        return refreshTokenUseCase.verifyAndGenerateNewAccess(token)
                .map(newAccessToken -> {
                    ResponseCookie newAccessCookie = createCookie("accessToken", newAccessToken, "/", jwtTokenService.getJwtExpirationInSeconds()); // 15 min

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                            .<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping("/logout")
//    public ResponseEntity<Void> logout() {
//        ResponseCookie delAccess = ResponseCookie.from("accessToken", "").path("/").maxAge(0).build();
//        ResponseCookie delRefresh = ResponseCookie.from("refreshToken", "").path("/api/auth/refresh").maxAge(0).build();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, delAccess.toString())
//                .header(HttpHeaders.SET_COOKIE, delRefresh.toString())
//                .build();
//    }
    public ResponseEntity<Void> logout() {
        try {
            ResponseCookie delAccess = createCookie("accessToken", "", "/", 0);
            ResponseCookie delRefresh = createCookie("refreshToken", "", "/api/auth/refresh", 0);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, delAccess.toString()) // Sin el .toString() extra
                    .header(HttpHeaders.SET_COOKIE, delRefresh.toString())
                    .build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    // Helper para no repetir código de cookies
    private ResponseCookie createCookie(String name, String value, String path, long maxAge) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(true)       // Necesario para SameSite=None
                .sameSite("None")   // Permite que funcione aunque el front/back difieran en subdominio
                .build();
    }

    public record LoginRequest(String username, String password) {
    }
}