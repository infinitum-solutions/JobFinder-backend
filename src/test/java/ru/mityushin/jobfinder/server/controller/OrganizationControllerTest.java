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
import ru.mityushin.jobfinder.server.dto.OrganizationDTO;
import ru.mityushin.jobfinder.server.service.organization.OrganizationService;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class OrganizationControllerTest {
    private final UUID DEFAULT_UUID = UUID.randomUUID();
    private OrganizationDTO defaultOrganizationDTO;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationController organizationController;

    @Configuration
    static class ContextConfiguration {
        @Bean
        public OrganizationService organizationService() {
            return Mockito.mock(OrganizationService.class);
        }
        @Bean
        public OrganizationController organizationController(OrganizationService organizationService) {
            return new OrganizationController(organizationService);
        }
    }

    @Before
    public void before() {
        defaultOrganizationDTO = OrganizationDTO.builder()
                .uuid(DEFAULT_UUID)
                .creatorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .subscribersCount(0)
                .build();
    }

    @After
    public void after() {
        Mockito.reset(organizationService);
    }

    @Test
    public void getOrganizations() {
        organizationController.getOrganizations();
        Mockito.verify(organizationService, Mockito.atLeastOnce()).findAll();
    }

    @Test
    public void createOrganization() {
        organizationController.createOrganization(defaultOrganizationDTO);
        Mockito.verify(organizationService, Mockito.atLeastOnce()).create(defaultOrganizationDTO);
    }

    @Test
    public void getOrganization() {
        organizationController.getOrganization(DEFAULT_UUID);
        Mockito.verify(organizationService, Mockito.atLeastOnce()).find(DEFAULT_UUID);
    }

    @Test
    public void updateOrganization() {
        organizationController.updateOrganization(DEFAULT_UUID, defaultOrganizationDTO);
        Mockito.verify(organizationService, Mockito.atLeastOnce()).update(DEFAULT_UUID, defaultOrganizationDTO);
    }

    @Test
    public void deleteOrganization() {
        organizationController.deleteOrganization(DEFAULT_UUID);
        Mockito.verify(organizationService, Mockito.atLeastOnce()).delete(DEFAULT_UUID);
    }

    @Test
    public void getSubscribers() {
        organizationController.getSubscribers(DEFAULT_UUID);
        Mockito.verify(organizationService, Mockito.atLeastOnce()).getSubscribers(DEFAULT_UUID);
    }

    @Test
    public void subscribe() {
        organizationController.subscribe(DEFAULT_UUID);
        Mockito.verify(organizationService, Mockito.atLeastOnce()).subscribe(DEFAULT_UUID);
    }

    @Test
    public void unsubscribe() {
        organizationController.unsubscribe(DEFAULT_UUID);
        Mockito.verify(organizationService, Mockito.atLeastOnce()).unsubscribe(DEFAULT_UUID);
    }
}