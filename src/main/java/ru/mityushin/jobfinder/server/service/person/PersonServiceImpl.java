package ru.mityushin.jobfinder.server.service.person;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;
import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.model.Role;
import ru.mityushin.jobfinder.server.repo.PersonRepository;
import ru.mityushin.jobfinder.server.repo.PublicationRepository;
import ru.mityushin.jobfinder.server.repo.RoleRepository;
import ru.mityushin.jobfinder.server.service.role.RoleService;
import ru.mityushin.jobfinder.server.dto.PersonDTO;
import ru.mityushin.jobfinder.server.util.exception.PermissionDeniedException;
import ru.mityushin.jobfinder.server.util.exception.data.DataAlreadyExistsException;
import ru.mityushin.jobfinder.server.util.exception.data.DataNotFoundException;
import ru.mityushin.jobfinder.server.util.exception.data.MissingRequiredParametersException;
import ru.mityushin.jobfinder.server.util.mapper.PersonMapper;
import ru.mityushin.jobfinder.server.util.mapper.PublicationMapper;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PublicationRepository publicationRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;

    @Override
    public Collection<PersonDTO> findAll() {
        return personRepository.findAll().stream()
                .filter(o -> !o.getDeleted())
                .map(PersonMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public PersonDTO find(UUID uuid) {
        Person person = personRepository.findByUuid(uuid);
        if (person == null
                || person.getDeleted()) {
            throw new DataNotFoundException("This profile has been deleted or has not been created yet.");
        }
        return PersonMapper.map(person);
    }

    @Transactional
    @Override
    public PersonDTO createAdmin(PersonDTO personDTO) {
        return createWithRoles(personDTO, roleService.getAdminRoles());
    }

    @Transactional
    @Override
    public PersonDTO createUser(PersonDTO personDTO) {
        return createWithRoles(personDTO, roleService.getUserRoles());
    }

    private PersonDTO createWithRoles(PersonDTO personDTO, Set<Role> roles) {
        if (personDTO.getUsername() == null) {
            throw new MissingRequiredParametersException("Required parameter 'username' doesn't specified.");
        }
        if (personDTO.getPassword() == null) {
            throw new MissingRequiredParametersException("Required parameter 'password' doesn't specified.");
        }
        if (personRepository.existsByUsername(personDTO.getUsername())) {
            throw new DataAlreadyExistsException("User with specified username already exists.");
        }
        Person person = PersonMapper.map(personDTO);
        person.setUuid(UUID.randomUUID());
        person.setPassword(encoder.encode(personDTO.getPassword()));
        person.setOrganizations(new HashSet<>());
        person.setRoles(roles);
        person.setDeleted(Boolean.FALSE);
        person.setLocked(Boolean.FALSE);
        person.setEnabled(Boolean.TRUE);

        Person saved = personRepository.save(person);
        return PersonMapper.map(saved);
    }

    @Transactional
    @Override
    public PersonDTO update(UUID uuid, PersonDTO personDTO) {
        Person personFromRepo = personRepository.findByUuid(uuid);
        if (isInaccessible(personFromRepo)) {
            throw new DataNotFoundException("This profile has been deleted or has not been created yet.");
        }
        if (personDTO.getOldPassword() != null
                && !encoder.encode(personDTO.getOldPassword()).equals(personFromRepo.getPassword())) {
            throw new PermissionDeniedException("Old password is invalid");
        }
        Person prepared = mergePersonDtoAndEncodePassword(personFromRepo, personDTO, encoder);
        Person saved = personRepository.save(prepared);
        return PersonMapper.map(saved);
    }

    @Transactional
    @Override
    public PersonDTO delete(UUID uuid) {
        Person person = personRepository.findByUuid(uuid);
        if (isInaccessible(person)) {
            throw new DataNotFoundException("This profile has been deleted or has not been created yet.");
        }
        person.setDeleted(Boolean.TRUE);
        return PersonMapper.map(personRepository.save(person));
    }

    @Transactional
    @Override
    public PersonDTO addRoleToPerson(UUID uuid, PersonDTO personDTO) {
        Person person = personRepository.findByUuid(uuid);
        if (person == null) {
            throw new DataNotFoundException("This profile has been deleted or has not been created yet.");
        }
        for (String role : personDTO.getRoles()) {
            Role roleFromRepo = roleRepository.findByName(role);
            if (roleFromRepo == null) {
                throw new DataNotFoundException("Role " + role + " has been deleted or has not been created yet.");
            }
            boolean added = person.getRoles().add(roleFromRepo);
            if (!added) {
                throw new DataAlreadyExistsException("This person already has role " + role);
            }
        }
        return PersonMapper.map(personRepository.save(person));
    }

    @Override
    public PersonDTO deleteRoleFromPerson(UUID uuid, String role) {
        Person person = personRepository.findByUuid(uuid);
        if (person == null) {
            throw new DataNotFoundException("This profile has been deleted or has not been created yet.");
        }
        Role roleFromRepo = roleRepository.findByName(role);
        if (roleFromRepo == null) {
            throw new DataNotFoundException("Role " + role + " has been deleted or has not been created yet.");
        }
        boolean removed = person.getRoles().remove(roleFromRepo);
        if (!removed) {
            throw new DataAlreadyExistsException("This person hasn't role " + role + " yet.");
        }
        return PersonMapper.map(personRepository.save(person));
    }

    @Override
    public Collection<PublicationDTO> findPersonPublications(UUID uuid) {
        return publicationRepository.findAllByAuthorUuid(uuid).stream()
                .map(PublicationMapper::map)
                .collect(Collectors.toList());
    }

    private static boolean isInaccessible(@Nullable Person person) {
        return person == null
                || person.getDeleted()
                || person.getLocked()
                || !person.getEnabled();
    }

    private static Person mergePersonDtoAndEncodePassword(Person person, PersonDTO personDTO, PasswordEncoder encoder) {
        return Person.builder()
                .id(person.getId())
                .uuid(person.getUuid())
                .username(personDTO.getUsername() == null ? person.getUsername() : personDTO.getUsername())
                .password(personDTO.getPassword() == null ? person.getPassword() : encoder.encode(personDTO.getPassword()))
                .deleted(person.getDeleted())
                .locked(person.getLocked())
                .enabled(person.getEnabled())
                .expire(person.getExpire())
                .credentialsExpire(person.getCredentialsExpire())
                .build();
    }
}
