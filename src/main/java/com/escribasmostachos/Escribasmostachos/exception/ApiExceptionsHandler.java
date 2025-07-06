package com.escribasmostachos.Escribasmostachos.exception;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionsHandler {

    @ExceptionHandler(ResourceAlreadyExistsOnDatabaseException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceAlreadyExistsOnDatabaseException(ResourceAlreadyExistsOnDatabaseException ex) {
        log.error("Resource already exists: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDTO<Void>(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ResourceDontExistsOnDatabaseException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceDontExistsOnDatabaseException(ResourceDontExistsOnDatabaseException ex) {
        log.error("Resource don't exists: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<Void>(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(InvalidIsbnException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleInvalidIsbnException(InvalidIsbnException ex) {
        log.error("Invalid ISBN exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<Void>(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MaxCurrentBooksReachedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleMaxCurrentBooksReached(MaxCurrentBooksReachedException ex) {
        log.warn("Max current books reached: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<Void>(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedUserActionException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleUnauthorizedUserActionException(UnauthorizedUserActionException ex) {
        log.warn("Unauthorized user action: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponseDTO<Void>(HttpStatus.FORBIDDEN, ex.getMessage()));
    }
    
    // API requests exceptions
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<Void>(HttpStatus.BAD_REQUEST, "Username or password not valid"));
    }
    
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO<Void>(HttpStatus.FORBIDDEN, "Access Denied: You do not have permission to access this resource"));
    }

   @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>>handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<Void>(HttpStatus.BAD_REQUEST, "Malformed JSON"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<Void>>handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        String message = String.format("The parameter " + name+ " must be of type " + type);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<Void>(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleInvalidRequestValidationException(HandlerMethodValidationException ex) {
    String errorMessage = ex.getAllErrors()
            .stream()
            .map(error -> error.getDefaultMessage())
            .findFirst()
            .orElse("Validation error");
        log.error("API request have some errors: " + errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<Void>(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleInvalidRequestValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        log.error("API request have some errors: " + errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<Void>(HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity error: " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDTO<Void>(HttpStatus.CONFLICT, "Data integrity error"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("Username not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<Void>(HttpStatus.NOT_FOUND, "User not found"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericException(Exception ex) {
        log.error("Internal server error: " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<Void>(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
    }
}
