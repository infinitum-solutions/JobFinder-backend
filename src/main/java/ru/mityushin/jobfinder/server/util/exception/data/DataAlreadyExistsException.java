package ru.mityushin.jobfinder.server.util.exception.data;

import ru.mityushin.jobfinder.server.util.exception.ServerException;

public class DataAlreadyExistsException extends ServerException {
    public DataAlreadyExistsException(String msg) {
        super(msg);
    }
}
