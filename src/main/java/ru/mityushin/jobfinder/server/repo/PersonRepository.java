package ru.mityushin.jobfinder.server.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mityushin.jobfinder.server.model.Person;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Collection<Person> findAll();
    Person findByUuid(UUID uuid);
    Person findByUsername(String username);
    boolean existsByUsername(String username);
}
