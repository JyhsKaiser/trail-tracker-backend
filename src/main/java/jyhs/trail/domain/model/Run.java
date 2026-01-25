package jyhs.trail.domain.model;

import java.time.LocalDate;

/**
 * Representa una carrera de trail.
 * Usamos Record por su inmutabilidad y sintaxis limpia.
 */
public record Run(
        Long id,
        String name,
        double distanceKm,
        int elevationGain,
        LocalDate date,
        Long userId // <-- El dueño de la ruta
) {}