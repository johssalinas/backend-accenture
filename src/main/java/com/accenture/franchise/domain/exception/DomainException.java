package com.accenture.franchise.domain.exception;

/** Excepción base para errores del dominio. */
public class DomainException extends RuntimeException {

  /** Crea la excepción de dominio con un mensaje descriptivo. */
  public DomainException(String message) {
    super(message);
  }

  /** Crea la excepción de dominio con mensaje y causa original. */
  public DomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
