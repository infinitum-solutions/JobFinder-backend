package ru.mityushin.jobfinder.server.service.publication;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;
import ru.mityushin.jobfinder.server.model.Publication;
import ru.mityushin.jobfinder.server.repo.PublicationRepository;
import ru.mityushin.jobfinder.server.util.JobFinderUtils;
import ru.mityushin.jobfinder.server.util.exception.PermissionDeniedException;
import ru.mityushin.jobfinder.server.util.exception.data.DataNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({
        UUID.class,
        JobFinderUtils.class,
        PublicationServiceImpl.class
})
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class PublicationServiceImplTest {
    private static final UUID DEFAULT_UUID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
    private Publication defaultPublication;
    private PublicationDTO defaultPublicationDTO;
    private PublicationDTO newPublicationDTO;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationService publicationService;

    @Configuration
    static class ContextConfiguration {
        @Bean
        public PublicationService publicationService(PublicationRepository repository) {
            return new PublicationServiceImpl(repository);
        }

        @Bean
        public PublicationRepository publicationRepository() {
            return Mockito.mock(PublicationRepository.class);
        }
    }

    @Before
    public void before() {
        defaultPublication = Publication.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .content("Content-content-content")
                .visible(true)
                .deleted(false)
                .build();
        defaultPublicationDTO = PublicationDTO.builder()
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .content("Content-content-content")
                .visible(true)
                .build();
        newPublicationDTO = PublicationDTO.builder()
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .content("Content")
                .visible(true)
                .build();
        mockStatic(JobFinderUtils.class);
        when(publicationRepository.save(any(Publication.class))).then(returnsFirstArg());
    }

    @Test
    public void findAll() {
        when(publicationRepository.findAll()).thenReturn(Collections.singletonList(defaultPublication));
        Collection<PublicationDTO> publications = publicationService.findAll();
        assertEquals(Collections.singletonList(defaultPublicationDTO), publications);
    }

    @Test(expected = DataNotFoundException.class)
    public void findWithoutUuid() {
        publicationService.find(null);
    }

    @Test(expected = DataNotFoundException.class)
    public void findDeleted() {
        defaultPublication.setDeleted(true);
        when(publicationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPublication);
        publicationService.find(DEFAULT_UUID);
    }

    @Test
    public void find() {
        when(publicationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPublication);
        assertEquals(defaultPublicationDTO, publicationService.find(DEFAULT_UUID));
    }

    @Test
    public void create() {
        PowerMockito.mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(DEFAULT_UUID);
        when(JobFinderUtils.getPrincipalIdentifier()).thenReturn(DEFAULT_UUID);
        assertEquals(defaultPublicationDTO, publicationService.create(defaultPublicationDTO));
    }

    @Test(expected = DataNotFoundException.class)
    public void updateWithoutUuid() {
        publicationService.update(null, newPublicationDTO);
    }

    @Test(expected = DataNotFoundException.class)
    public void updateDeleted() {
        defaultPublication.setDeleted(true);
        when(publicationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPublication);
        publicationService.update(DEFAULT_UUID, newPublicationDTO);
    }

    @Test(expected = PermissionDeniedException.class)
    public void updateWithoutPermissions() {
        when(JobFinderUtils.getPrincipalIdentifier()).thenReturn(UUID.randomUUID());
        when(publicationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPublication);
        publicationService.update(DEFAULT_UUID, newPublicationDTO);
    }

    @Test
    public void update() {
        when(JobFinderUtils.getPrincipalIdentifier()).thenReturn(DEFAULT_UUID);
        when(publicationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPublication);
        assertEquals(newPublicationDTO, publicationService.update(DEFAULT_UUID, newPublicationDTO));
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteNonExistingPublication() {
        when(publicationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPublication);
        publicationService.delete(UUID.randomUUID());
    }

    @Test
    public void deleteExistingPublication() {
        when(publicationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultPublication);
        when(JobFinderUtils.getPrincipalIdentifier()).thenReturn(DEFAULT_UUID);
        PublicationDTO dto = publicationService.delete(DEFAULT_UUID);
        assertEquals(defaultPublicationDTO, dto);
    }
}