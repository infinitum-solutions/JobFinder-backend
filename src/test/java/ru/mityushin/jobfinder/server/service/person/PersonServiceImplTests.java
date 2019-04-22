package ru.mityushin.jobfinder.server.service.person;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.dto.PersonDTO;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;
import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.model.Publication;
import ru.mityushin.jobfinder.server.model.Role;
import ru.mityushin.jobfinder.server.repo.PersonRepository;
import ru.mityushin.jobfinder.server.repo.PublicationRepository;
import ru.mityushin.jobfinder.server.repo.RoleRepository;
import ru.mityushin.jobfinder.server.service.role.RoleService;
import ru.mityushin.jobfinder.server.util.JobFinderUtils;
import ru.mityushin.jobfinder.server.util.enums.Sex;
import ru.mityushin.jobfinder.server.util.exception.PermissionDeniedException;
import ru.mityushin.jobfinder.server.util.exception.data.DataAlreadyExistsException;
import ru.mityushin.jobfinder.server.util.exception.data.DataNotFoundException;
import ru.mityushin.jobfinder.server.util.exception.data.MissingRequiredParametersException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({
        UUID.class,
        JobFinderUtils.class,
        PersonServiceImpl.class
})
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class PersonServiceImplTests {
    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private Person defaultPerson;
    private PersonDTO defaultPersonDto;
    private PersonDTO defaultAdminDto;
    private PersonDTO defaultUserDto;
    private PersonDTO newPersonDto;
    private PersonDTO newPersonWithOldPswdDto;
    private Role adminRole;
    private Role userRole;
    private Publication defaultPublication;
    private PublicationDTO defaultPublicationDTO;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private PersonService personService;

    @Configuration
    static class ContextConfiguration {
        @Bean
        public PersonRepository personRepository() {
            return PowerMockito.mock(PersonRepository.class);
        }

        @Bean
        public PublicationRepository publicationRepository() {
            return PowerMockito.mock(PublicationRepository.class);
        }

        @Bean
        public RoleRepository roleRepository() {
            return PowerMockito.mock(RoleRepository.class);
        }

        @Bean
        public RoleService roleService() {
            return PowerMockito.mock(RoleService.class);
        }

        @Bean
        public PasswordEncoder encoder() {
            return PowerMockito.mock(PasswordEncoder.class);
        }

        @Bean
        public PersonService personService(PersonRepository personRepository,
                                           PublicationRepository publicationRepository,
                                           RoleRepository roleRepository,
                                           RoleService roleService,
                                           PasswordEncoder encoder) {
            return new PersonServiceImpl(personRepository, publicationRepository, roleRepository, roleService, encoder);
        }

    }

    @Before
    public void before() {
        defaultPerson = Person.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .username("user")
                .password("pswd")
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .country("Russia")
                .deleted(false)
                .locked(false)
                .enabled(true)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        defaultPersonDto = PersonDTO.builder()
                .uuid(DEFAULT_UUID)
                .username("user")
                .password("pswd")
                .oldPassword(null)
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .country("Russia")
                .build();
        adminRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .persons(new HashSet<>())
                .build();
        userRole = Role.builder()
                .id(1L)
                .name("USER")
                .persons(new HashSet<>())
                .build();
        defaultAdminDto = PersonDTO.builder()
                .uuid(DEFAULT_UUID)
                .username("user")
                .password("pswd")
                .oldPassword(null)
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .roles(Collections.singletonList("ADMIN"))
                .country("Russia")
                .build();
        defaultUserDto = PersonDTO.builder()
                .uuid(DEFAULT_UUID)
                .username("user")
                .password("pswd")
                .oldPassword(null)
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .roles(Collections.singletonList("USER"))
                .country("Russia")
                .build();
        newPersonDto = PersonDTO.builder()
                .uuid(DEFAULT_UUID)
                .username("resu")
                .password("pswd")
                .oldPassword(null)
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .country("Russia")
                .build();
        newPersonWithOldPswdDto = PersonDTO.builder()
                .uuid(DEFAULT_UUID)
                .username("resu")
                .password("pswd")
                .oldPassword("pass")
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .country("Russia")
                .build();
        defaultPublication = Publication.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("title")
                .description("desc")
                .content("content")
                .visible(true)
                .deleted(false)
                .build();
        defaultPublicationDTO = PublicationDTO.builder()
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("title")
                .description("desc")
                .content("content")
                .visible(true)
                .build();

        PowerMockito.when(personRepository.save(Mockito.any(Person.class))).then(returnsFirstArg());
        PowerMockito.when(encoder.encode(Mockito.anyString())).then(returnsFirstArg());
        PowerMockito.when(roleService.getAdminRoles()).thenReturn(Stream.of(adminRole)
                .collect(Collectors.toCollection(HashSet::new)));
        PowerMockito.when(roleService.getAdminRoles()).thenReturn(Stream.of(userRole)
                .collect(Collectors.toCollection(HashSet::new)));
        PowerMockito.when(roleRepository.findByName("ADMIN")).thenReturn(adminRole);
        PowerMockito.when(roleRepository.findByName("USER")).thenReturn(userRole);
    }

    @Test
    public void findAll() {
        PowerMockito.when(personRepository.findAll()).thenReturn(Collections.singletonList(defaultPerson));
        Collection<PersonDTO> persons = personService.findAll();
        assertEquals(Collections.singletonList(defaultPersonDto), persons);
    }

    @Test(expected = DataNotFoundException.class)
    public void findWithoutUuid() {
        personService.find(null);
    }

    @Test(expected = DataNotFoundException.class)
    public void findDeleted() {
        defaultPerson.setDeleted(true);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.find(DEFAULT_UUID);
    }

    @Test
    public void find() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        assertEquals(defaultPersonDto, personService.find(DEFAULT_UUID));
    }

    @Test(expected = MissingRequiredParametersException.class)
    public void createAdminWithoutUsername() {
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(false);
        personService.createAdmin(PersonDTO.builder().password("pswd").build());
    }

    @Test(expected = MissingRequiredParametersException.class)
    public void createAdminWithoutPassword() {
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(false);
        personService.createAdmin(PersonDTO.builder().username("user").build());
    }

    @Test(expected = DataAlreadyExistsException.class)
    public void createAdminExisting() {
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(true);
        personService.createAdmin(defaultPersonDto);
    }

    @Test
    public void createAdmin() {
        PowerMockito.mockStatic(UUID.class);
        PowerMockito.when(UUID.randomUUID()).thenReturn(DEFAULT_UUID);
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(false);
        assertEquals(defaultAdminDto, personService.createAdmin(defaultPersonDto));
    }


    @Test(expected = MissingRequiredParametersException.class)
    public void createUserWithoutUsername() {
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(false);
        personService.createUser(PersonDTO.builder().password("pswd").build());
    }

    @Test(expected = MissingRequiredParametersException.class)
    public void createUserWithoutPassword() {
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(false);
        personService.createUser(PersonDTO.builder().username("user").build());
    }

    @Test(expected = DataAlreadyExistsException.class)
    public void createUserExisting() {
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(true);
        personService.createUser(defaultPersonDto);
    }

    @Test
    public void createUser() {
        PowerMockito.mockStatic(UUID.class);
        PowerMockito.when(UUID.randomUUID()).thenReturn(DEFAULT_UUID);
        PowerMockito.when(personRepository.existsByUsername(defaultPersonDto.getUsername())).thenReturn(false);
        assertEquals(defaultUserDto, personService.createUser(defaultPersonDto));
    }

    @Test(expected = DataNotFoundException.class)
    public void getCurrentNotExist() {
        PowerMockito.mockStatic(JobFinderUtils.class);
        PowerMockito.when(JobFinderUtils.getPrincipalIdentifier()).thenReturn(DEFAULT_UUID);
        personService.getCurrent();
    }

    @Test
    public void getCurrent() {
        PowerMockito.mockStatic(JobFinderUtils.class);
        PowerMockito.when(JobFinderUtils.getPrincipalIdentifier()).thenReturn(DEFAULT_UUID);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        assertEquals(defaultPersonDto, personService.getCurrent());
    }

    @Test(expected = DataNotFoundException.class)
    public void updateWithoutUuid() {
        personService.update(null, newPersonDto);
    }

    @Test(expected = DataNotFoundException.class)
    public void updateDeleted() {
        defaultPerson.setDeleted(true);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.update(DEFAULT_UUID, newPersonDto);
    }

    @Test(expected = DataNotFoundException.class)
    public void updateLocked() {
        defaultPerson.setLocked(true);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.update(DEFAULT_UUID, newPersonDto);
    }

    @Test(expected = DataNotFoundException.class)
    public void updateDisabled() {
        defaultPerson.setEnabled(false);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.update(DEFAULT_UUID, newPersonDto);
    }

    @Test(expected = PermissionDeniedException.class)
    public void updateWithoutPermissions() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.update(DEFAULT_UUID, newPersonWithOldPswdDto);
    }

    @Test
    public void update() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        assertEquals(newPersonDto, personService.update(DEFAULT_UUID, newPersonDto));
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteWithoutUuid() {
        personService.delete(null);
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteDeleted() {
        defaultPerson.setDeleted(true);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.delete(DEFAULT_UUID);
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteLocked() {
        defaultPerson.setLocked(true);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.delete(DEFAULT_UUID);
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteDisabled() {
        defaultPerson.setEnabled(false);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.delete(DEFAULT_UUID);
    }

    @Test
    public void delete() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        assertEquals(defaultPersonDto, personService.delete(DEFAULT_UUID));
        assertTrue(defaultPerson.getDeleted());
    }

    @Test(expected = DataNotFoundException.class)
    public void addRoleToPersonWithoutUuid() {
        personService.addRoleToPerson(null, PersonDTO.builder().roles(Stream.of("USER", "ADMIN")
                .collect(Collectors.toCollection(ArrayList::new))).build());
    }

    @Test(expected = DataNotFoundException.class)
    public void addUnsupportedRoleToPerson() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.addRoleToPerson(DEFAULT_UUID, PersonDTO.builder().roles(Stream.of("LUSER")
                .collect(Collectors.toCollection(ArrayList::new))).build());
    }

    @Test(expected = DataAlreadyExistsException.class)
    public void addRoleToPersonThatHeAlreadyHas() {
        defaultPerson.getRoles().add(userRole);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.addRoleToPerson(DEFAULT_UUID, PersonDTO.builder().roles(Stream.of("USER")
                .collect(Collectors.toCollection(ArrayList::new))).build());
    }

    @Test
    public void addRoleToPerson() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        assertEquals(defaultUserDto, personService.addRoleToPerson(DEFAULT_UUID, PersonDTO.builder().roles(Stream.of("USER")
                .collect(Collectors.toCollection(ArrayList::new))).build()));
        ;
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteRolePersonWithoutUuid() {
        personService.deleteRoleFromPerson(null, "USER");
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteUnsupportedRoleFromPerson() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.deleteRoleFromPerson(DEFAULT_UUID, "LUSER");
    }

    @Test(expected = DataAlreadyExistsException.class)
    public void deleteRoleFromPersonThatHeAlreadyHas() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        personService.deleteRoleFromPerson(DEFAULT_UUID, "USER");
    }

    @Test
    public void deleteRoleFromPerson() {
        defaultPerson.getRoles().add(userRole);
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        assertEquals(defaultUserDto, personService.deleteRoleFromPerson(DEFAULT_UUID, "USER"));
    }

    @Test(expected = DataNotFoundException.class)
    public void findPersonPublicationsWithoutUuid() {
        personService.findPersonPublications(null);
    }

    @Test
    public void findPersonPublications() {
        PowerMockito.when(publicationRepository.findAllByAuthorUuid(DEFAULT_UUID)).thenReturn(
                Collections.singletonList(defaultPublication));
        assertEquals(Collections.singletonList(defaultPublicationDTO), personService.findPersonPublications(DEFAULT_UUID));
    }
}
