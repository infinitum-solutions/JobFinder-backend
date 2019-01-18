package ru.mityushin.jobfinder.server.listener;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.mityushin.jobfinder.server.model.Organization;
import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.model.Publication;
import ru.mityushin.jobfinder.server.model.Role;
import ru.mityushin.jobfinder.server.repo.OrganizationRepository;
import ru.mityushin.jobfinder.server.repo.PersonRepository;
import ru.mityushin.jobfinder.server.repo.PublicationRepository;
import ru.mityushin.jobfinder.server.repo.RoleRepository;
import ru.mityushin.jobfinder.server.service.role.RoleService;
import ru.mityushin.jobfinder.server.util.enums.SecurityRole;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final PersonRepository personRepository;
    private final OrganizationRepository organizationRepository;
    private final PublicationRepository publicationRepository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    private final Logger log;

    private boolean hasAlreadySetup = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!hasAlreadySetup) {
            for (SecurityRole securityRole : SecurityRole.values()) {
                createRoleIfDoesntExists(securityRole.name());
            }
            Person admin = createPersonIfDoesntExists("admin",
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    roleService.getAdminRoles());
            createPersonIfDoesntExists("moderator",
                    UUID.fromString("00000000-0000-0000-0000-000000000001"),
                    roleService.getModeratorRoles());
            Person user = createPersonIfDoesntExists("user",
                    UUID.fromString("00000000-0000-0000-0000-000000000002"),
                    roleService.getUserRoles());
            createDefaultOrganizationsIfDoesntExists(admin.getUuid(), user.getUuid());
            createDefaultPublicationsIfDoesntExists(admin.getUuid(), user.getUuid());
            hasAlreadySetup = true;
        }
    }

    private Person createPersonIfDoesntExists(String username, UUID uuid, Set<Role> roles) {
        Person person = personRepository.findByUsername(username);
        if (person == null) {
            log.info("Creating person {}.", username);
            return personRepository.save(Person.builder()
                    .uuid(uuid)
                    .username(username)
                    .password(encoder.encode("password"))
                    .firstName(username)
                    .lastName("Test")
                    .roles(roles)
                    .deleted(Boolean.FALSE)
                    .locked(Boolean.FALSE)
                    .enabled(Boolean.TRUE)
                    .build());
        }
        log.info("Can't create person {}. Reason: person is already exists.", username);
        return person;
    }

    private Role createRoleIfDoesntExists(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            log.info("Creating role {}.", name);
            return roleRepository.save(Role.builder()
                    .name(name)
                    .build());
        }
        log.info("Can't create role {}. Reason: role is already exists.", name);
        return role;
    }

    private Organization createOrganization(String title, UUID creatorUuid) {
        log.info("Creating organization {}.", title);
        Organization organization = Organization.builder()
                .uuid(UUID.randomUUID())
                .creatorUuid(creatorUuid)
                .title(title)
                .description(creatorUuid.toString())
                .deleted(false)
                .build();
        return organizationRepository.save(organization);
    }

    private Collection<String> findAllOrganizationTitlesByCreator(UUID creator) {
        return organizationRepository.findAllByCreatorUuid(creator).stream()
                .map(Organization::getTitle)
                .collect(Collectors.toList());
    }

    private void createDefaultOrganizationsIfDoesntExists(UUID admin, UUID user) {
        List<String> defaultAdminOrganizationTitles = Arrays.asList("JobFinder", "Infinitum Solutions", "Test organization");
        List<String> defaultUserOrganizationTitles = Arrays.asList("JobFinder fun page", "Infinitum Solutions fun page", "Test organization fun page");
        Collection<String> adminOrganizationTitles = findAllOrganizationTitlesByCreator(admin);
        Collection<String> userOrganizationTitles = findAllOrganizationTitlesByCreator(user);
        defaultAdminOrganizationTitles.stream()
                .filter(title -> !adminOrganizationTitles.contains(title))
                .map(title -> createOrganization(title, admin))
                .count();
        defaultUserOrganizationTitles.stream()
                .filter(title -> !userOrganizationTitles.contains(title))
                .map(title -> createOrganization(title, user))
                .count();
    }

    private Publication createPublication(String title, UUID authorUuid) {
        log.info("Creating publication {}.", title);
        UUID uuid = UUID.randomUUID();
        Publication publication = Publication.builder()
                .uuid(uuid)
                .authorUuid(authorUuid)
                .title(title)
                .description("My uuid is ".concat(uuid.toString()))
                .content(title.concat("'s author uuid is ").concat(authorUuid.toString()))
                .visible(true)
                .deleted(false)
                .build();
        return publicationRepository.save(publication);
    }

    private Collection<String> findAllPublicationTitlesByAuthor(UUID author) {
        return publicationRepository.findAllByAuthorUuid(author).stream()
                .map(Publication::getTitle)
                .collect(Collectors.toList());
    }

    private void createDefaultPublicationsIfDoesntExists(UUID admin, UUID user) {
        List<String> defaultAdminPublicationTitles = Arrays.asList("JobFinder", "Infinitum Solutions", "Test organization");
        List<String> defaultUserPublicationTitles = Arrays.asList("JobFinder fun page", "Infinitum Solutions fun page", "Test organization fun page");
        Collection<String> adminPublicationTitles = findAllPublicationTitlesByAuthor(admin);
        Collection<String> userPublicationTitles = findAllPublicationTitlesByAuthor(user);
        defaultAdminPublicationTitles.stream()
                .filter(title -> !adminPublicationTitles.contains(title))
                .map(title -> createPublication(title, admin))
                .count();

        defaultUserPublicationTitles.stream()
                .filter(title -> !userPublicationTitles.contains(title))
                .map(title -> createPublication(title, user))
                .count();
    }

}
