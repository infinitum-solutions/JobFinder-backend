package ru.mityushin.jobfinder.server.service.role;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.mityushin.jobfinder.server.model.Role;
import ru.mityushin.jobfinder.server.repo.RoleRepository;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class RoleServiceImplTests {
    private Set<Role> roles;
    private Set<Role> rolesUser;
    private Set<Role> rolesModerator;
    private Set<Role> rolesAdmin;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    @Configuration
    public static class ContextConfiguration {
        @Bean
        public RoleRepository roleRepository() {
            return PowerMockito.mock(RoleRepository.class);
        }

        @Bean
        public RoleService roleService(RoleRepository roleRepository) {
            return new RoleServiceImpl(roleRepository);
        }
    }

    @Before
    public void before() {
        rolesUser = new HashSet<>();
        rolesUser.add(Role.builder().id(1L).name("ROLE_USER").persons(new HashSet<>()).build());
        rolesUser.add(Role.builder().id(2L).name("ROLE_ORGANIZATION_MANAGER").persons(new HashSet<>()).build());
        rolesUser.add(Role.builder().id(3L).name("ROLE_CONTENT_MAKER").persons(new HashSet<>()).build());
        rolesModerator = new HashSet<>();
        rolesModerator.addAll(rolesUser);
        rolesModerator.add(Role.builder().id(4L).name("ROLE_MODERATOR").persons(new HashSet<>()).build());
        rolesAdmin = new HashSet<>();
        rolesAdmin.addAll(rolesModerator);
        rolesAdmin.add(Role.builder().id(5L).name("ROLE_ADMIN").persons(new HashSet<>()).build());
        roles = new HashSet<>();
        roles.addAll(rolesAdmin);

        PowerMockito.when(roleRepository.findAll()).thenReturn(roles);
    }

    @Test
    public void getAdminRoles() {
        assertEquals(rolesAdmin, roleService.getAdminRoles());
    }

    @Test
    public void getModeratorRoles() {
        assertEquals(rolesModerator, roleService.getModeratorRoles());
    }

    @Test
    public void getUserRoles() {
        assertEquals(rolesUser, roleService.getUserRoles());
    }
}
