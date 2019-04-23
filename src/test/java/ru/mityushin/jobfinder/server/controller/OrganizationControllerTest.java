package ru.mityushin.jobfinder.server.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.service.organization.OrganizationService;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class OrganizationControllerTest {

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

    @Test
    public void getOrganizations() {
        organizationController.getOrganizations();
        Mockito.verify(organizationService, Mockito.atLeastOnce()).findAll();
    }

}