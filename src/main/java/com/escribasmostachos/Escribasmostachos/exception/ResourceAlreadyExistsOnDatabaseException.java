package com.escribasmostachos.Escribasmostachos.exception;

public class ResourceAlreadyExistsOnDatabaseException extends RuntimeException {
    public ResourceAlreadyExistsOnDatabaseException(String message) {
        super(message);
    }
}