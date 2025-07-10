package com.escribasmostachos.Escribasmostachos.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.ISBNValidator;

public class IsbnValidator implements ConstraintValidator<IsbnValid, String> {

    private ISBNValidator validator = new ISBNValidator();

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        String normalizedIsbn = isbn.replaceAll("[-\\s]", "");
        return validator.isValid(normalizedIsbn);
    }
}
