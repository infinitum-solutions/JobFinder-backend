package ru.mityushin.jobfinder.server.service.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mityushin.jobfinder.server.model.Role;
import ru.mityushin.jobfinder.server.repo.RoleRepository;
import ru.mityushin.jobfinder.server.util.enums.SecurityRole;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private Set<Role> adminRoles;
    private Set<Role> moderatorRoles;
    private Set<Role> userRoles;

    private boolean hasAlreadyLoaded = false;

    @Override
    public Set<Role> getAdminRoles() {
        checkLoadRoles();
        return adminRoles;
    }

    @Override
    public Set<Role> getModeratorRoles() {
        checkLoadRoles();
        return moderatorRoles;
    }

    @Override
    public Set<Role> getUserRoles() {
        checkLoadRoles();
        return userRoles;
    }

    private void checkLoadRoles() {
        if (!hasAlreadyLoaded) {
            Set<Role> roles = roleRepository.findAll();
            adminRoles = roles.stream()
                    .filter(role -> SecurityRole.isDefaultAdminSecurityRole(role.getName()))
                    .collect(Collectors.toSet());
            moderatorRoles = roles.stream()
                    .filter(role -> SecurityRole.isDefaultModeratorSecurityRole(role.getName()))
                    .collect(Collectors.toSet());
            userRoles = roles.stream()
                    .filter(role -> SecurityRole.isDefaultUserSecurityRole(role.getName()))
                    .collect(Collectors.toSet());
            hasAlreadyLoaded = true;
        }
    }
}
