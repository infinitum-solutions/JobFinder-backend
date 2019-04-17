package ru.mityushin.jobfinder.server.service.person;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.dto.PersonDTO;
import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.repo.PersonRepository;
import ru.mityushin.jobfinder.server.repo.PublicationRepository;
import ru.mityushin.jobfinder.server.repo.RoleRepository;
import ru.mityushin.jobfinder.server.service.role.RoleService;
import ru.mityushin.jobfinder.server.util.enums.Sex;
import ru.mityushin.jobfinder.server.util.exception.data.DataNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({
})
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class PersonServiceImplTests {
    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private Person defaultPerson;
    private Person defaultDeletedPerson;
    private PersonDTO defaultPersonDto;

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
                .build();
        defaultDeletedPerson = Person.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .username("user")
                .password("pswd")
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .country("Russia")
                .deleted(true)
                .locked(false)
                .enabled(true)
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
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultDeletedPerson);
        personService.find(DEFAULT_UUID);
    }

    @Test
    public void find() {
        PowerMockito.when(personRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPerson);
        assertEquals(defaultPersonDto, personService.find(DEFAULT_UUID));
    }
}
