package ru.mityushin.jobfinder.server.util.exception;

public class PermissionDeniedException extends ServerException {
    public PermissionDeniedException(String msg) {
        super(msg);
    }
}
