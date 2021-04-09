package com.square.blogging.repository;

import com.square.blogging.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(Role.RoleName name);
}
