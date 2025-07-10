package com.escribasmostachos.Escribasmostachos.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsbnValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsbnValid {
    String message() default "Invalid ISBN";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}