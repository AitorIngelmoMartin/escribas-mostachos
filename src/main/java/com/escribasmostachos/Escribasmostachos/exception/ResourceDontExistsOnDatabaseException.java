package com.escribasmostachos.Escribasmostachos.exception;

public class ResourceDontExistsOnDatabaseException extends RuntimeException {
    public ResourceDontExistsOnDatabaseException(String message) {
        super(message);
    }
}