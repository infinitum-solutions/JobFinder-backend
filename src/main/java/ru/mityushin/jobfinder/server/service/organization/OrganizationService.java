package ru.mityushin.jobfinder.server.service.organization;

import ru.mityushin.jobfinder.server.dto.OrganizationDTO;
import ru.mityushin.jobfinder.server.dto.PersonDTO;

import java.util.Collection;
import java.util.UUID;

public interface OrganizationService {
    Collection<OrganizationDTO> findAll();
    OrganizationDTO find(UUID uuid);
    OrganizationDTO create(OrganizationDTO organizationDTO);
    OrganizationDTO update(UUID uuid, OrganizationDTO organizationDTO);
    OrganizationDTO delete(UUID uuid);
    Collection<PersonDTO> getSubscribers(UUID uuid);
    OrganizationDTO subscribe(UUID uuid);
    OrganizationDTO unsubscribe(UUID uuid);
}
