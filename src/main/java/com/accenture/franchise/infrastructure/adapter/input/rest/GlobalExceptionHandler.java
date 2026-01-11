package com.accenture.franchise.infrastructure.adapter.input.rest;

import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.DomainException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Manejador global de excepciones. */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /** Maneja la excepción cuando una entidad no se encuentra. */
  @ExceptionHandler(EntityNotFoundException.class)
  public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
    log.error("Entity not found: {}", ex.getMessage());

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Entity Not Found");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }

  /** Maneja violaciones de reglas de negocio. */
  @ExceptionHandler(BusinessRuleViolationException.class)
  public ProblemDetail handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
    log.error("Business rule violation: {}", ex.getMessage());

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problemDetail.setTitle("Business Rule Violation");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }

  /** Maneja errores de validación. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
    log.error("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
    problemDetail.setTitle("Validation Error");
    problemDetail.setProperty("errors", errors);
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }

  /** Maneja excepciones de argumentos inválidos. */
  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("Illegal argument: {}", ex.getMessage());

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problemDetail.setTitle("Invalid Request");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }

  /** Maneja excepciones de conversión de tipos en parámetros de método. */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ProblemDetail handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    log.error("Type mismatch: {}", ex.getMessage());

    String message =
        String.format(
            "Invalid value '%s' for parameter '%s'. Expected type: %s",
            ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    problemDetail.setTitle("Invalid Request Parameter");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }

  /** Maneja excepciones de tipo de contenido no soportado. */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ProblemDetail handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException ex) {
    log.error("Unsupported media type: {}", ex.getMessage());

    String message = "Content-Type header is required and must be 'application/json'";

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    problemDetail.setTitle("Unsupported Media Type");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }

  /** Maneja excepciones del dominio. */
  @ExceptionHandler(DomainException.class)
  public ProblemDetail handleDomainException(DomainException ex) {
    log.error("Domain exception: {}", ex.getMessage());

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    problemDetail.setTitle("Domain Error");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }

  /** Maneja excepciones genéricas no controladas. */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
  }
}
