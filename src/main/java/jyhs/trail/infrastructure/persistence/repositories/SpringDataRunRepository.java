package jyhs.trail.infrastructure.persistence.repositories;

import jyhs.trail.infrastructure.persistence.entities.RunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataRunRepository extends JpaRepository<RunEntity, Long> {
    List<RunEntity> findByUserId(Long userId);
    // Aquí podrías agregar consultas personalizadas si las necesitas
}