package jyhs.trail.domain.repository;

import jyhs.trail.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(User user);

    Optional<User> findById(Long userId);
}
