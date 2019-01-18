package ru.mityushin.jobfinder.server.util.enums;

public enum SecurityRole {
    ROLE_ADMIN,
    ROLE_CONTENT_MAKER,
    ROLE_MODERATOR,
    ROLE_USER,
    ROLE_ORGANIZATION_MANAGER;

    public static boolean isDefaultAdminSecurityRole(String name) {
        return name != null;
    }

    public static boolean isDefaultModeratorSecurityRole(String name) {
        switch (SecurityRole.valueOf(name)) {
            case ROLE_MODERATOR:
            case ROLE_USER:
            case ROLE_ORGANIZATION_MANAGER:
            case ROLE_CONTENT_MAKER: {
                return true;
            }
        }
        return false;
    }

    public static boolean isDefaultUserSecurityRole(String name) {
        switch (SecurityRole.valueOf(name)) {
            case ROLE_USER:
            case ROLE_ORGANIZATION_MANAGER:
            case ROLE_CONTENT_MAKER: {
                return true;
            }
        }
        return false;
    }
}