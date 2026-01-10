package com.accenture.franchise.domain.exception;

import java.util.UUID;

/** Excepción lanzada cuando una entidad no se encuentra. */
public class EntityNotFoundException extends DomainException {

  /** Crea la excepción indicando el nombre de la entidad y su identificador. */
  public EntityNotFoundException(String entityName, UUID id) {
    super(String.format("%s not found with id: %s", entityName, id));
  }

  /** Crea la excepción con un mensaje personalizado. */
  public EntityNotFoundException(String message) {
    super(message);
  }
}
