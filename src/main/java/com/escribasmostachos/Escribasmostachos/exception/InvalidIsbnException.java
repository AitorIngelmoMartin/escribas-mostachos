package com.escribasmostachos.Escribasmostachos.exception;

public class InvalidIsbnException extends RuntimeException {
    public InvalidIsbnException(String message) {
        super(message);
    }
}