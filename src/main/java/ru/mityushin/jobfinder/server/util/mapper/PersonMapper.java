package ru.mityushin.jobfinder.server.util.mapper;

import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.dto.PersonDTO;

import javax.validation.constraints.NotNull;

public class PersonMapper {

    @NotNull
    public static Person map(PersonDTO personDTO) {
        return Person.builder()
                .uuid(personDTO.getUuid())
                .username(personDTO.getUsername())
                .password(personDTO.getPassword())
                .firstName(personDTO.getFirstName())
                .lastName(personDTO.getLastName())
                .sex(personDTO.getSex())
                .country(personDTO.getCountry())
                .build();
    }

    @NotNull
    public static PersonDTO map(Person person) {
        return PersonDTO.builder()
                .uuid(person.getUuid())
                .username(person.getUsername())
                .password(person.getPassword())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .sex(person.getSex())
                .country(person.getCountry())
                .build();
    }
}
