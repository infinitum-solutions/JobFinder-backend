package ru.mityushin.jobfinder.server.util.exception.data;

import ru.mityushin.jobfinder.server.util.exception.ServerException;

public class DataNotFoundException extends ServerException {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
