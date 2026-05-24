package com.bde.adminprocessing.blocked;

import java.nio.file.Path;

public class MissingBlockedOperationsFileException extends RuntimeException {

    public MissingBlockedOperationsFileException(Path path) {
        super("Blocked operations fixture file not found: " + path.toAbsolutePath());
    }
}
