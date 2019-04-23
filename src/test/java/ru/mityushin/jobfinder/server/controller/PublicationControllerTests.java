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
import ru.mityushin.jobfinder.server.dto.PublicationDTO;
import ru.mityushin.jobfinder.server.service.publication.PublicationService;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class PublicationControllerTests {
    private final UUID DEFAULT_UUID = UUID.randomUUID();
    private PublicationDTO defaultPublicationDTO;

    @Autowired
    private PublicationService publicationService;
    @Autowired
    private PublicationController publicationController;

    @Configuration
    public static class ContextConfiguration {
        @Bean
        public PublicationService personService() {
            return Mockito.mock(PublicationService.class);
        }

        @Bean
        public PublicationController personController(PublicationService publicationService) {
            return new PublicationController(publicationService);
        }
    }

    @Before
    public void before() {
        defaultPublicationDTO = PublicationDTO.builder()
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .content("Content-content-content")
                .visible(true)
                .build();
    }

    @After
    public void after() {
        Mockito.reset(publicationService);
    }

    @Test
    public void getPublications() {
        publicationController.getPublications();
        Mockito.verify(publicationService, Mockito.atLeastOnce()).findAll();
    }

    @Test
    public void createPublication() {
        publicationController.createPublication(defaultPublicationDTO);
        Mockito.verify(publicationService, Mockito.atLeastOnce()).create(defaultPublicationDTO);
    }

    @Test
    public void getPublication() {
        publicationController.getPublication(DEFAULT_UUID);
        Mockito.verify(publicationService, Mockito.atLeastOnce()).find(DEFAULT_UUID);
    }

    @Test
    public void updatePublication() {
        publicationController.updatePublication(DEFAULT_UUID, defaultPublicationDTO);
        Mockito.verify(publicationService, Mockito.atLeastOnce()).update(DEFAULT_UUID, defaultPublicationDTO);
    }

    @Test
    public void deletePublication() {
        publicationController.deletePublication(DEFAULT_UUID);
        Mockito.verify(publicationService, Mockito.atLeastOnce()).delete(DEFAULT_UUID);
    }
}
