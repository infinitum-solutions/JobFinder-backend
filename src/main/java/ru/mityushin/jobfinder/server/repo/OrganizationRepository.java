package ru.mityushin.jobfinder.server.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mityushin.jobfinder.server.model.Organization;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Long> {
    Collection<Organization> findAll();
    Collection<Organization> findAllByCreatorUuid(UUID creatorUuid);
    Organization findByUuid(UUID uuid);
}
