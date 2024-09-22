package com.teamr.runardo.accounts.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourName, String fieldName, String fieldValue) {
        super(String.format("%s not found with the given input data %s: '%s'", resourName, fieldName, fieldValue));
    }
}
