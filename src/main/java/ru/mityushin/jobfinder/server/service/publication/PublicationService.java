package ru.mityushin.jobfinder.server.service.publication;

import ru.mityushin.jobfinder.server.dto.PublicationDTO;

import java.util.Collection;
import java.util.UUID;

public interface PublicationService {
    Collection<PublicationDTO> findAll();
    PublicationDTO find(UUID uuid);
    PublicationDTO create(PublicationDTO publicationDTO);
    PublicationDTO update(UUID uuid, PublicationDTO publicationDTO);
    PublicationDTO delete(UUID uuid);
}
