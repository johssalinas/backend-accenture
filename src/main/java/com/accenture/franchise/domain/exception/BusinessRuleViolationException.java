package com.accenture.franchise.domain.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio
 */
public class BusinessRuleViolationException extends DomainException {
    
    public BusinessRuleViolationException(String message) {
        super(message);
    }
    
    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
