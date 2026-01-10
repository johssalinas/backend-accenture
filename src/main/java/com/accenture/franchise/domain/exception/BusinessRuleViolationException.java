package com.accenture.franchise.domain.exception;

/** Excepción lanzada cuando se viola una regla de negocio. */
public class BusinessRuleViolationException extends DomainException {

  /** Crea la excepción indicando el mensaje de violación de regla. */
  public BusinessRuleViolationException(String message) {
    super(message);
  }

  /** Crea la excepción incluyendo la causa original. */
  public BusinessRuleViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}
