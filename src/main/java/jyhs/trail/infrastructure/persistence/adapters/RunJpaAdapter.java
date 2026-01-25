package jyhs.trail.infrastructure.persistence.adapters;

import jyhs.trail.domain.model.Run;
import jyhs.trail.domain.repository.RunRepository;
import jyhs.trail.infrastructure.persistence.entities.RunEntity;
import jyhs.trail.infrastructure.persistence.entities.UserEntity;
import jyhs.trail.infrastructure.persistence.repositories.SpringDataRunRepository;
import jyhs.trail.infrastructure.persistence.repositories.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RunJpaAdapter implements RunRepository {

    private final SpringDataRunRepository jpaRepository;
    private final SpringDataUserRepository userJpaRepository;

    public RunJpaAdapter(SpringDataRunRepository jpaRepository,  SpringDataUserRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Run save(Run run) {
        // 1. Mapeamos de Dominio (Record) a Entidad JPA
        RunEntity entity = new RunEntity();
        entity.setName(run.name());
        entity.setDistanceKm(run.distanceKm());
        entity.setElevationGain(run.elevationGain());
        entity.setDate(run.date());

        // Debemos buscar la entidad del usuario para que Hibernate sepa a quién pertenece
        UserEntity userEntity = userJpaRepository.findById(run.userId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado al persistir carrera"));
        entity.setUser(userEntity); // Aquí es donde se llena el user_id

        // 2. Guardamos en la BD
        RunEntity savedEntity = jpaRepository.save(entity);

        // 3. Devolvemos al Dominio convertido de nuevo a Record
        return new Run(
                savedEntity.getId(),
                savedEntity.getName(),
                savedEntity.getDistanceKm(),
                savedEntity.getElevationGain(),
                savedEntity.getDate(),
                savedEntity.getUser().getId()
        );
    }

    @Override
    public List<Run> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(entity -> new Run(
                        entity.getId(),
                        entity.getName(),
                        entity.getDistanceKm(),
                        entity.getElevationGain(),
                        entity.getDate(),
                        entity.getUser().getId() // Pasamos el ID del usuario al modelo de dominio
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}