package ru.mityushin.jobfinder.server.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mityushin.jobfinder.server.model.Role;

import java.util.Set;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Set<Role> findAll();
    Role findByName(String name);
}
