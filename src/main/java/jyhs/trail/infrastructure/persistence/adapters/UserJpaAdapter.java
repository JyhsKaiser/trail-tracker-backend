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

    @Transactional
    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(
                null,
                user.username(),
                user.email(),
                user.password()
        );
        UserEntity saved = jpaRepository.save(entity);
        return new User(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getPassword());
    }

    @Transactional
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(u -> new User(u.getId(), u.getUsername(), u.getEmail(), u.getPassword()));
    }
}