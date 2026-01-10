package com.accenture.franchise.domain.repository;

import com.accenture.franchise.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port de salida para persistencia de productos. */
public interface ProductRepository {

  /** Guarda un producto y devuelve la entidad persistida. */
  Product save(Product product);

  /** Busca un producto por su identificador. */
  Optional<Product> findById(UUID id);

  /** Obtiene todos los productos de una sucursal. */
  List<Product> findByBranchId(UUID branchId);

  /** Elimina un producto por su identificador. */
  void deleteById(UUID id);

  /** Verifica si existe un producto por nombre dentro de una sucursal. */
  boolean existsByNameAndBranchId(String name, UUID branchId);

  /** Verifica si existe un producto por su identificador. */
  boolean existsById(UUID id);
}
