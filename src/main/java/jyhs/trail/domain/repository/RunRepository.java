package jyhs.trail.domain.repository;

import jyhs.trail.domain.model.Run;

import java.util.List;

public interface RunRepository {
    Run save(Run run);
    List<Run> findByUserId(Long userId); // <-- Cambio clave
    void deleteById(Long id);
}