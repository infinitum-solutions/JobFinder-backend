package ru.mityushin.jobfinder.server.util.exception.data;

import ru.mityushin.jobfinder.server.util.exception.ServerException;

public class MissingRequiredParametersException extends ServerException {
    public MissingRequiredParametersException(String msg) {
        super(msg);
    }
}
