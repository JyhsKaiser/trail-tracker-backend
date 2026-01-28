package jyhs.trail.domain.repository;

import jyhs.trail.domain.model.Run;

import java.util.List;
import java.util.Optional;

public interface RunRepository {
    Run save(Run run);
    Optional<Run> findById(Long id);
    List<Run> findByUserId(Long userId); // <-- Cambio clave
    void deleteById(Long id);
}