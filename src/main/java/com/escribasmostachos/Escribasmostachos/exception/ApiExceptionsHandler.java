package com.escribasmostachos.Escribasmostachos.exception;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionsHandler {

    @ExceptionHandler(ResourceAlreadyExistsOnDatabaseException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleResourceAlreadyExistsOnDatabaseException(ResourceAlreadyExistsOnDatabaseException ex) {
        log.error("Resource already exists: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDto<Void>(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ResourceDontExistsOnDatabaseException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleResourceDontExistsOnDatabaseException(ResourceDontExistsOnDatabaseException ex) {
        log.error("Resource don't exists: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(InvalidIsbnException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleInvalidIsbnException(InvalidIsbnException ex) {
        log.error("Invalid ISBN exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(IncoherentOperationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleIncoherentOperationException(IncoherentOperationException ex) {
        log.error("Incoherent operation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, "Incoherent operation: " + ex.getMessage()));
    }

    // API requests exceptions
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, "Username or password not valid"));
    }

   @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<Void>>handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, "Malformed JSON"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDto<Void>>handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        String message = String.format("The parameter " + name+ " must be of type " + type);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleInvalidRequestValidationException(HandlerMethodValidationException ex) {
    String errorMessage = ex.getAllErrors()
            .stream()
            .map(error -> error.getDefaultMessage())
            .findFirst()
            .orElse("Validation error");
        log.error("API request have some errors: " + errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleInvalidRequestValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        log.error("API request have some errors: " + errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<Void>(HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity error: " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDto<Void>(HttpStatus.CONFLICT, "Data integrity error"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("Username not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDto<Void>(HttpStatus.NOT_FOUND, "User not found"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGenericException(Exception ex) {
        log.error("Internal server error: " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<Void>(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
    }
}
