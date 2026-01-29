package jyhs.trail.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jyhs.trail.domain.service.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getServletPath();
        String token = null;

        // ✅ 1. EXCLUSIÓN CORRECTA: No procesar tokens en rutas públicas
        if (requestPath.contains("/api/auth/login") ||
                requestPath.contains("/api/users/register") ||
                requestPath.contains("/api/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. BUSCAR COOKIE: Optimizado
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // ✅ 3. VALIDACIÓN: Solo si el token existe y es válido
        if (token != null && !token.isEmpty() && tokenService.validateToken(token)) {
            String username = tokenService.getUsernameFromToken(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}