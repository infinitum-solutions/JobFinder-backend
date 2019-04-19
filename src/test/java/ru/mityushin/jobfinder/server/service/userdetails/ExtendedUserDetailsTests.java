package ru.mityushin.jobfinder.server.service.userdetails;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.model.Person;
import ru.mityushin.jobfinder.server.model.Role;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static junit.framework.TestCase.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class ExtendedUserDetailsTests {
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pswd";
    private static final Boolean DEFAULT_LOCKED = false;
    private static final Boolean DEFAULT_ENABLED = true;
    private static final UUID DEFAULT_UUID = UUID.randomUUID();

    private Set<Role> roles;
    private Set<SimpleGrantedAuthority> authorities;

    @Autowired
    private Person person;
    @Autowired
    private ExtendedUserDetails userDetails;

    @Configuration
    public static class ContextConfiguration {
        @Bean
        public Person person() {
            return PowerMockito.mock(Person.class);
        }

        @Bean
        public ExtendedUserDetails userDetails(Person person) {
            return new ExtendedUserDetails(person);
        }
    }

    @Before
    public void before() {
        roles = new HashSet<>();
        roles.add(Role.builder().id(1L).name("ROLE_USER").persons(new HashSet<>()).build());
        roles.add(Role.builder().id(2L).name("ROLE_ORGANIZATION_MANAGER").persons(new HashSet<>()).build());
        roles.add(Role.builder().id(3L).name("ROLE_CONTENT_MAKER").persons(new HashSet<>()).build());
        roles.add(Role.builder().id(4L).name("ROLE_MODERATOR").persons(new HashSet<>()).build());
        roles.add(Role.builder().id(5L).name("ROLE_ADMIN").persons(new HashSet<>()).build());

        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ORGANIZATION_MANAGER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_CONTENT_MAKER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        PowerMockito.when(person.getUsername()).thenReturn(USERNAME);
        PowerMockito.when(person.getPassword()).thenReturn(PASSWORD);
        PowerMockito.when(person.getRoles()).thenReturn(roles);
        PowerMockito.when(person.getLocked()).thenReturn(DEFAULT_LOCKED);
        PowerMockito.when(person.getEnabled()).thenReturn(DEFAULT_ENABLED);
        PowerMockito.when(person.getUuid()).thenReturn(DEFAULT_UUID);
    }

    @After
    public void after() {
        Mockito.reset(person);
    }

    @Test
    public void getAuthorities() {
        assertEquals(authorities, userDetails.getAuthorities());
    }

    @Test
    public void getPassword() {
        assertEquals(PASSWORD, userDetails.getPassword());
    }

    @Test
    public void getUsername() {
        assertEquals(USERNAME, userDetails.getUsername());
    }

    @Test
    public void isAccountNonExpiredWithoutExpire() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    public void isAccountNonExpiredNotExpired() {
        PowerMockito.when(person.getExpire()).thenReturn(ZonedDateTime.now().plusHours(2));
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    public void isAccountNonExpiredExpired() {
        PowerMockito.when(person.getExpire()).thenReturn(ZonedDateTime.now().minusHours(2));
        assertFalse(userDetails.isAccountNonExpired());
    }

    @Test
    public void isAccountNonLocked() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    public void isCredentialsNonExpiredWithoutExpire() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    public void isCredentialsNonExpiredNotExpired() {
        PowerMockito.when(person.getCredentialsExpire()).thenReturn(ZonedDateTime.now().plusHours(2));
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    public void isCredentialsNonExpiredExpired() {
        PowerMockito.when(person.getCredentialsExpire()).thenReturn(ZonedDateTime.now().minusHours(2));
        assertFalse(userDetails.isCredentialsNonExpired());
    }

    @Test
    public void isEnabled() {
        assertTrue(userDetails.isEnabled());
    }

    @Test
    public void getIdentifier() {
        assertEquals(DEFAULT_UUID, userDetails.getIdentifier());
    }
}
