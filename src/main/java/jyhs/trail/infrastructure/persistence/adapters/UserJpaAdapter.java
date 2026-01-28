package jyhs.trail.infrastructure.persistence.adapters;

import jakarta.transaction.Transactional;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.UserRepository;
import jyhs.trail.infrastructure.persistence.entities.UserEntity;
import jyhs.trail.infrastructure.persistence.repositories.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserJpaAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;

    public UserJpaAdapter(SpringDataUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity();

        // Si el Record trae ID, Hibernate hará un UPDATE
        if (user.id() != null) {
            entity.setId(user.id());
        }

        entity.setUsername(user.username());
        entity.setEmail(user.email());
        entity.setPassword(user.password()); // El password ya viene hasheado o es el anterior

        UserEntity savedEntity = jpaRepository.save(entity);

        return new User(
                savedEntity.getId(),
                savedEntity.getUsername(),
                savedEntity.getEmail(),
                savedEntity.getPassword()
        );
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaRepository.findById(userId)
                .map(u ->  new User(u.getId(), u.getUsername(), u.getEmail(), u.getPassword()));
    }

    @Transactional
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(u -> new User(u.getId(), u.getUsername(), u.getEmail(), u.getPassword()));
    }
}