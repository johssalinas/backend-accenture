package com.accenture.franchise.domain.repository;

import com.accenture.franchise.domain.model.Branch;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port de salida para persistencia de sucursales. */
public interface BranchRepository {

  /** Guarda una sucursal y devuelve la entidad persistida. */
  Branch save(Branch branch);

  /** Busca una sucursal por su identificador. */
  Optional<Branch> findById(UUID id);

  /** Obtiene todas las sucursales asociadas a una franquicia. */
  List<Branch> findByFranchiseId(UUID franchiseId);

  /** Elimina una sucursal por su identificador. */
  void deleteById(UUID id);

  /** Verifica si existe una sucursal con nombre dentro de una franquicia. */
  boolean existsByNameAndFranchiseId(String name, UUID franchiseId);

  /** Verifica si existe una sucursal por su identificador. */
  boolean existsById(UUID id);
}
