package com.accenture.franchise.infrastructure.adapter.output.persistence.repository;

import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.ProductEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repositorio JPA para productos. */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

  /** Obtiene todos los productos de una sucursal. */
  List<ProductEntity> findByBranchId(UUID branchId);

  /** Verifica si existe un producto con nombre en una sucursal. */
  boolean existsByNameAndBranchId(String name, UUID branchId);

  /** Obtiene los productos de una sucursal ordenados por stock. */
  @Query("SELECT p FROM ProductEntity p WHERE p.branch.id = :branchId ORDER BY p.stock DESC")
  List<ProductEntity> findByBranchIdOrderByStockDesc(@Param("branchId") UUID branchId);
}
