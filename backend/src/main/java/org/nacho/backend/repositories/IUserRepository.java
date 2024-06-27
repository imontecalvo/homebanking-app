package org.nacho.backend.repositories;

import org.nacho.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findUserByUsername(String username);
}
