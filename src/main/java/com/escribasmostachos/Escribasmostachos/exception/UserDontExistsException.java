package com.escribasmostachos.Escribasmostachos.exception;

public class UserDontExistsException extends RuntimeException {
    public UserDontExistsException(String message) {
        super(message);
    }
}