package ru.mityushin.jobfinder.server.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.dto.PersonDTO;
import ru.mityushin.jobfinder.server.service.person.PersonService;
import ru.mityushin.jobfinder.server.util.enums.Sex;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class PersonControllerTests {
    private final UUID DEFAULT_UUID = UUID.randomUUID();
    private PersonDTO defaultPersonDto;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonController personController;

    @Configuration
    public static class ContextConfiguration {
        @Bean
        public PersonService personService() {
            return Mockito.mock(PersonService.class);
        }

        @Bean
        public PersonController personController(PersonService personService) {
            return new PersonController(personService);
        }
    }

    @Before
    public void before() {
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

    @After
    public void after() {
        Mockito.reset(personService);
    }

    @Test
    public void getPersons() {
        personController.getPersons();
        Mockito.verify(personService, Mockito.atLeastOnce()).findAll();
    }

    @Test
    public void createUser() {
        personController.createUser(defaultPersonDto);
        Mockito.verify(personService, Mockito.atLeastOnce()).createUser(defaultPersonDto);
    }

    @Test
    public void createAdmin() {
        personController.createAdmin(defaultPersonDto);
        Mockito.verify(personService, Mockito.atLeastOnce()).createAdmin(defaultPersonDto);
    }

    @Test
    public void getCurrentPerson() {
        personController.getCurrentPerson();
        Mockito.verify(personService, Mockito.atLeastOnce()).getCurrent();
    }

    @Test
    public void getPerson() {
        personController.getPerson(DEFAULT_UUID);
        Mockito.verify(personService, Mockito.atLeastOnce()).find(DEFAULT_UUID);
    }

    @Test
    public void updatePerson() {
        personController.updatePerson(DEFAULT_UUID, defaultPersonDto);
        Mockito.verify(personService, Mockito.atLeastOnce()).update(DEFAULT_UUID, defaultPersonDto);
    }

    @Test
    public void deletePerson() {
        personController.deletePerson(DEFAULT_UUID);
        Mockito.verify(personService, Mockito.atLeastOnce()).delete(DEFAULT_UUID);
    }

    @Test
    public void getPersonPublications() {
        personController.getPersonPublications(DEFAULT_UUID);
        Mockito.verify(personService, Mockito.atLeastOnce()).findPersonPublications(DEFAULT_UUID);
    }

    @Test
    public void addRole() {
        personController.createPersonRole(DEFAULT_UUID, defaultPersonDto);
        Mockito.verify(personService, Mockito.atLeastOnce()).addRoleToPerson(DEFAULT_UUID, defaultPersonDto);
    }

    @Test
    public void deleteRole() {
        personController.createPersonRole(DEFAULT_UUID, "ROLE");
        Mockito.verify(personService, Mockito.atLeastOnce()).deleteRoleFromPerson(DEFAULT_UUID, "ROLE");
    }
}
