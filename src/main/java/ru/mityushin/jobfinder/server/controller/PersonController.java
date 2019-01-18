package ru.mityushin.jobfinder.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;
import ru.mityushin.jobfinder.server.service.person.PersonService;
import ru.mityushin.jobfinder.server.dto.PersonDTO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @Secured({"ROLE_USER"})
    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<PersonDTO>> getPersons() {
        return new ResponseEntity<>(personService.findAll(), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping(value = "/admin")
    @ResponseBody
    public ResponseEntity<PersonDTO> createAdmin(@Valid @RequestBody PersonDTO personDTO) {
        return new ResponseEntity<>(personService.createAdmin(personDTO), HttpStatus.CREATED);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<PersonDTO> createUser(@Valid @RequestBody PersonDTO personDTO) {
        return new ResponseEntity<>(personService.createUser(personDTO), HttpStatus.CREATED);
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<PersonDTO> getPerson(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(personService.find(uuid), HttpStatus.OK);
    }

    @Secured({"ROLE_USER"})
    @PutMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable(value = "uuid") UUID uuid,
                                                  @Valid @RequestBody PersonDTO personDTO) {
        return new ResponseEntity<>(personService.update(uuid, personDTO), HttpStatus.OK);
    }

    @Secured({"ROLE_USER"})
    @DeleteMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<PersonDTO> deletePerson(@PathVariable(value = "uuid") UUID uuid) {
        return new ResponseEntity<>(personService.delete(uuid), HttpStatus.OK);
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/{uuid}/publications")
    @ResponseBody
    public ResponseEntity<Collection<PublicationDTO>> getPersonPublications(@PathVariable(value = "uuid") UUID uuid) {
        return new ResponseEntity<>(personService.findPersonPublications(uuid), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping(value = "/{uuid}/roles")
    @ResponseBody
    public ResponseEntity<PersonDTO> createPersonRole(@PathVariable(value = "uuid") UUID uuid,
                                                      @RequestBody PersonDTO personDTO) {
        return new ResponseEntity<>(personService.addRoleToPerson(uuid, personDTO), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping(value = "/{uuid}/roles/{role}")
    @ResponseBody
    public ResponseEntity<PersonDTO> createPersonRole(@PathVariable(value = "uuid") UUID uuid,
                                                      @PathVariable(value = "role") String role) {
        return new ResponseEntity<>(personService.deleteRoleFromPerson(uuid, role), HttpStatus.OK);
    }
}
