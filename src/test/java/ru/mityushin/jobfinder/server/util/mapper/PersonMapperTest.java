package ru.mityushin.jobfinder.server.util.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.mityushin.jobfinder.server.dto.PersonDTO;
import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.util.enums.Sex;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PersonMapperTest {
    private static final UUID DEFAULT_UUID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
    private PersonDTO defaultPersonDTO;
    private Person defaultPerson;

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
                .build();
        defaultPersonDTO = PersonDTO.builder()
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
    public void mapFromDto() {
        Person person = PersonMapper.map(defaultPersonDTO);
        assertEquals(defaultPerson, person);
    }

    @Test
    public void mapToDto() {
        PersonDTO personDTO = PersonMapper.map(defaultPerson);
        assertEquals(defaultPersonDTO, personDTO);
    }
}