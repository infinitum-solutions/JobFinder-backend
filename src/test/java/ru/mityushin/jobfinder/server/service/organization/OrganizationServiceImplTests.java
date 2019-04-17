package ru.mityushin.jobfinder.server.service.organization;

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
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.dto.OrganizationDTO;
import ru.mityushin.jobfinder.server.model.Organization;
import ru.mityushin.jobfinder.server.repo.OrganizationRepository;
import ru.mityushin.jobfinder.server.repo.PersonRepository;
import ru.mityushin.jobfinder.server.util.JobFinderUtils;
import ru.mityushin.jobfinder.server.util.exception.data.DataNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({
        JobFinderUtils.class
})
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class OrganizationServiceImplTests {
    private static final UUID DEFAULT_UUID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
    private static Organization defaultOrganization;
    private static Organization defaultDeletedOrganization;
    private static OrganizationDTO defaultOrganizationDTO;

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private Logger log;
    @Autowired
    private OrganizationService organizationService;

    @Configuration
    static class ContextConfiguration {
        @Bean
        public OrganizationRepository organizationRepository() {
            return Mockito.mock(OrganizationRepository.class);
        }

        @Bean
        public PersonRepository personRepository() {
            return Mockito.mock(PersonRepository.class);
        }

        @Bean
        public Logger log() {
            return Mockito.mock(Logger.class);
        }

        @Bean
        public OrganizationService organizationService(OrganizationRepository organizationRepository,
                                                       PersonRepository personRepository,
                                                       Logger log) {
            return new OrganizationServiceImpl(organizationRepository, personRepository, log);
        }
    }

    @BeforeClass
    public static void beforeClass() {
        defaultOrganization = Organization.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .creatorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .deleted(false)
                .subscribers(new HashSet<>())
                .build();
        defaultDeletedOrganization = Organization.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .creatorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .deleted(true)
                .subscribers(new HashSet<>())
                .build();
        defaultOrganizationDTO = OrganizationDTO.builder()
                .uuid(DEFAULT_UUID)
                .creatorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .subscribersCount(0)
                .build();
        PowerMockito.mockStatic(JobFinderUtils.class);
        PowerMockito.when(JobFinderUtils.getPrincipalIdentifier()).thenReturn(DEFAULT_UUID);
    }

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void findAll() {
        PowerMockito.when(organizationRepository.findAll()).thenReturn(Collections.singletonList(defaultOrganization));
        Collection<OrganizationDTO> organizations = organizationService.findAll();
        assertEquals(Collections.singletonList(defaultOrganizationDTO), organizations);
    }

    @Test(expected = DataNotFoundException.class)
    public void findWithoutUuid() {
        organizationService.find(null);
    }

    @Test(expected = DataNotFoundException.class)
    public void findDeleted() {
        PowerMockito.when(organizationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultDeletedOrganization);
        organizationService.find(DEFAULT_UUID);
    }

    @Test
    public void find() {
        PowerMockito.when(organizationRepository.findByUuid(DEFAULT_UUID)).thenReturn(defaultOrganization);
        assertEquals(defaultOrganizationDTO, organizationService.find(DEFAULT_UUID));
    }
}