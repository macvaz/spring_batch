package com.bde.adminprocessing.blocked;

import java.util.List;

public class BlockedOperationValidationException extends RuntimeException {

    private final List<String> errors;

    public BlockedOperationValidationException(List<String> errors) {
        super("Blocked operation validation failed: " + String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
