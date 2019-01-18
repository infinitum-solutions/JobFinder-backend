package ru.mityushin.jobfinder.server.service.person;

import ru.mityushin.jobfinder.server.dto.PersonDTO;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;

import java.util.Collection;
import java.util.UUID;

public interface PersonService {
    Collection<PersonDTO> findAll();
    PersonDTO find(UUID uuid);
    PersonDTO createAdmin(PersonDTO personDTO);
    PersonDTO createUser(PersonDTO personDTO);
    PersonDTO update(UUID uuid, PersonDTO personDTO);
    PersonDTO delete(UUID uuid);
    PersonDTO addRoleToPerson(UUID uuid, PersonDTO personDTO);
    PersonDTO deleteRoleFromPerson(UUID uuid, String role);
    Collection<PublicationDTO> findPersonPublications(UUID uuid);
}
