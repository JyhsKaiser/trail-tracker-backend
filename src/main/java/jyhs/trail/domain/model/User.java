package jyhs.trail.domain.model;

public record User(
        Long id,
        String username,
        String email,
        String password // En infraestructura la encriptaremos
) {}