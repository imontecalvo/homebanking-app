package org.nacho.backend.repositories;

import org.nacho.backend.models.User;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepository extends CrudRepository<User,Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
