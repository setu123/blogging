package com.square.blogging.repository;

import com.square.blogging.model.Role;
import com.square.blogging.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    //List<User> findByType(User.Type type);

    //List<User> findByRoles(Collection<Role> roles);

    //List<User> findAllByRolesIn(Collection<Role> roles);

    List<User> findAllByRolesName(Role.RoleName name);

    Optional<User> findByUsername(String username);
}
