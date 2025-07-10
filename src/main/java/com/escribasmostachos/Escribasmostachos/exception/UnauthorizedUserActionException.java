package com.escribasmostachos.Escribasmostachos.exception;

public class UnauthorizedUserActionException extends RuntimeException {
    public UnauthorizedUserActionException(String message) {
        super(message);
    }
}