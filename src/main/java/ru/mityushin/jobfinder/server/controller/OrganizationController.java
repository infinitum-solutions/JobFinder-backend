package ru.mityushin.jobfinder.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.mityushin.jobfinder.server.service.organization.OrganizationService;
import ru.mityushin.jobfinder.server.dto.OrganizationDTO;
import ru.mityushin.jobfinder.server.dto.PersonDTO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<OrganizationDTO>> getOrganizations() {
        return new ResponseEntity<>(organizationService.findAll(), HttpStatus.OK);
    }

    @Secured({"ROLE_ORGANIZATION_MANAGER"})
    @PostMapping
    @ResponseBody
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationDTO organizationDTO) {
        return new ResponseEntity<>(organizationService.create(organizationDTO), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<OrganizationDTO> getOrganization(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(organizationService.find(uuid), HttpStatus.OK);
    }

    @Secured({"ROLE_ORGANIZATION_MANAGER"})
    @PutMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable("uuid") UUID uuid,
                                               @Valid @RequestBody OrganizationDTO organizationDTO) {
        return new ResponseEntity<>(organizationService.update(uuid, organizationDTO), HttpStatus.OK);
    }

    @Secured({"ROLE_ORGANIZATION_MANAGER"})
    @DeleteMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<OrganizationDTO> deleteOrganization(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(organizationService.delete(uuid), HttpStatus.OK);
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/{uuid}/subscribers")
    @ResponseBody
    public ResponseEntity<Collection<PersonDTO>> getSubscribers(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(organizationService.getSubscribers(uuid), HttpStatus.OK);
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/{uuid}/subscribe")
    @ResponseBody
    public ResponseEntity<OrganizationDTO> subscribe(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(organizationService.subscribe(uuid), HttpStatus.CREATED);
    }

    @Secured({"ROLE_USER"})
    @DeleteMapping(value = "/{uuid}/subscribe")
    @ResponseBody
    public ResponseEntity<OrganizationDTO> unsubscribe(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(organizationService.unsubscribe(uuid), HttpStatus.OK);
    }
}
