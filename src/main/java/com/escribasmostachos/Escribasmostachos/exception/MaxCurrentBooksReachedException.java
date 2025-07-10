package com.escribasmostachos.Escribasmostachos.exception;

public class MaxCurrentBooksReachedException extends RuntimeException{
    public MaxCurrentBooksReachedException() {
        super("User has reached the maximum number of current books allowed");
    }
}
