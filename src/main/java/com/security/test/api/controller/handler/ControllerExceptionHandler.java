package com.security.test.api.controller.handler;

import com.security.test.api.dto.CustomErrorDTO;
import com.security.test.api.dto.ValidationErrorDTO;
import com.security.test.api.exception.ForbiddenException;
import com.security.test.api.exception.DatabaseException;
import com.security.test.api.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorDTO> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return buildErrorResponseFromException(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomErrorDTO> database(DatabaseException e, HttpServletRequest request) {
        return buildErrorResponseFromException(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDTO> methodArgumentNotValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(422);
        ValidationErrorDTO err = new ValidationErrorDTO(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CustomErrorDTO> forbidden(ForbiddenException e, HttpServletRequest request) {
        return buildErrorResponseFromException(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorDTO> accessDenied(AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponseFromMessage("Acesso negado: " + e.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    private ResponseEntity<CustomErrorDTO> buildErrorResponseFromException(RuntimeException e, HttpStatus status, HttpServletRequest request) {
        return buildErrorResponseFromMessage(e.getMessage(), status, request);
    }

    private ResponseEntity<CustomErrorDTO> buildErrorResponseFromMessage(String message, HttpStatus status, HttpServletRequest request) {
        CustomErrorDTO err = new CustomErrorDTO(Instant.now(), status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}