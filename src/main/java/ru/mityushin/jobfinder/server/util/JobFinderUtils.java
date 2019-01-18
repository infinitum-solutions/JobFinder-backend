package ru.mityushin.jobfinder.server.util;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.mityushin.jobfinder.server.service.userdetails.Identifiable;
import ru.mityushin.jobfinder.server.util.exception.UnsupportedIdentifierException;
import ru.mityushin.jobfinder.server.util.exception.UnsupportedPrincipalException;

import java.util.UUID;

public class JobFinderUtils {
    public static UUID getPrincipalIdentifier() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Identifiable) {
            Object identifier = ((Identifiable) principal).getIdentifier();
            if (identifier instanceof UUID) {
                return (UUID) identifier;
            } else {
                throw new UnsupportedIdentifierException(identifier.getClass().toString());
            }
        } else {
            throw new UnsupportedPrincipalException(principal.getClass().toString());
        }
    }
}
