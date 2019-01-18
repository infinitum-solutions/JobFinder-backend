package ru.mityushin.jobfinder.server.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mityushin.jobfinder.server.model.Publication;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface PublicationRepository extends CrudRepository<Publication, Long> {
    Collection<Publication> findAll();
    Collection<Publication> findAllByAuthorUuid(UUID authorUuid);
    Publication findByUuid(UUID uuid);
}
