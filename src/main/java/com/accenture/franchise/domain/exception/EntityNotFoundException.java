package com.accenture.franchise.domain.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando una entidad no se encuentra
 */
public class EntityNotFoundException extends DomainException {
    
    public EntityNotFoundException(String entityName, UUID id) {
        super(String.format("%s not found with id: %s", entityName, id));
    }
    
    public EntityNotFoundException(String message) {
        super(message);
    }
}
