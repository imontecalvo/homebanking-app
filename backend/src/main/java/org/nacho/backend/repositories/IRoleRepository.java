package org.nacho.backend.repositories;

import org.nacho.backend.models.roles_authorities.Role;
import org.nacho.backend.models.roles_authorities.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByName(RoleEnum name);
}
