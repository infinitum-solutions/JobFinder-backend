package ru.mityushin.jobfinder.server.service.userdetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.repo.PersonRepository;
import ru.mityushin.jobfinder.server.util.enums.Sex;

import java.util.HashSet;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class UserDetailsServiceImplTest {
    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final String DEFAULT_USERNAME = "username";
    private Person defaultPerson;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private UserDetailsService userDetailsService;

    @Configuration
    public static class ContextConfiguration {
        @Bean
        public PersonRepository personRepository() {
            return PowerMockito.mock(PersonRepository.class);
        }

        @Bean
        public UserDetailsServiceImpl userDetailsService(PersonRepository personRepository) {
            return new UserDetailsServiceImpl(personRepository);
        }
    }

    @Before
    public void before() {
        defaultPerson = Person.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .username(DEFAULT_USERNAME)
                .password("pswd")
                .firstName("name")
                .lastName("last")
                .sex(Sex.MALE)
                .country("Russia")
                .deleted(false)
                .locked(false)
                .enabled(true)
                .roles(new HashSet<>())
                .build();
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameWithoutUsername() {
        userDetailsService.loadUserByUsername(null);
    }

    @Test
    public void loadUserByUsername() {
        PowerMockito.when(personRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(defaultPerson);
        assertEquals(new ExtendedUserDetails(defaultPerson), userDetailsService.loadUserByUsername(DEFAULT_USERNAME));
    }
}
