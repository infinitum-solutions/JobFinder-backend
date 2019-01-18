package ru.mityushin.jobfinder.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.mityushin.jobfinder.server.service.publication.PublicationService;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<PublicationDTO>> getPublications() {
        return new ResponseEntity<>(publicationService.findAll(), HttpStatus.OK);
    }

    @Secured({"ROLE_CONTENT_MAKER"})
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createPublication(@Valid @RequestBody PublicationDTO publicationDTO) {
        return new ResponseEntity<>(publicationService.create(publicationDTO), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<PublicationDTO> getPublication(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(publicationService.find(uuid), HttpStatus.OK);
    }

    @Secured({"ROLE_MODERATOR", "ROLE_CONTENT_MAKER"})
    @PutMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<?> updatePublication(@PathVariable("uuid") UUID uuid,
                                               @Valid @RequestBody PublicationDTO publicationDTO) {
        return new ResponseEntity<>(publicationService.update(uuid, publicationDTO), HttpStatus.OK);
    }

    @Secured({"ROLE_MODERATOR", "ROLE_USER"})
    @DeleteMapping(value = "/{uuid}")
    @ResponseBody
    public ResponseEntity<?> deletePublication(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(publicationService.delete(uuid), HttpStatus.OK);
    }
}
