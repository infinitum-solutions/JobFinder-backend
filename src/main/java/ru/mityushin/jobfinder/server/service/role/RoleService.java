package ru.mityushin.jobfinder.server.service.role;

import ru.mityushin.jobfinder.server.model.Role;

import java.util.Set;

public interface RoleService {
    Set<Role> getAdminRoles();
    Set<Role> getModeratorRoles();
    Set<Role> getUserRoles();
}
