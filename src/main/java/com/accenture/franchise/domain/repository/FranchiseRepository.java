package com.accenture.franchise.domain.repository;

import com.accenture.franchise.domain.model.Franchise;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port de salida para persistencia de franquicias. */
public interface FranchiseRepository {

  /** Guarda una franquicia y devuelve la entidad persistida. */
  Franchise save(Franchise franchise);

  /** Busca una franquicia por su identificador. */
  Optional<Franchise> findById(UUID id);

  /** Obtiene todas las franquicias. */
  List<Franchise> findAll();

  /** Elimina una franquicia por su identificador. */
  void deleteById(UUID id);

  /** Verifica si existe una franquicia con el nombre indicado. */
  boolean existsByName(String name);

  /** Verifica si existe una franquicia por su identificador. */
  boolean existsById(UUID id);
}
