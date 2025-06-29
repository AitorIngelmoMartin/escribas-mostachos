package com.escribasmostachos.Escribasmostachos.exception;

public class BookDontExistsException extends RuntimeException {
    public BookDontExistsException(String message) {
        super(message);
    }
}