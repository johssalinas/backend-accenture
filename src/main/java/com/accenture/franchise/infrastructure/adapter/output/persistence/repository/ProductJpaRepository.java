package com.accenture.franchise.infrastructure.adapter.output.persistence.repository;

import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para Productos
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    
    List<ProductEntity> findByBranchId(UUID branchId);
    
    boolean existsByNameAndBranchId(String name, UUID branchId);
    
    @Query("SELECT p FROM ProductEntity p WHERE p.branch.id = :branchId ORDER BY p.stock DESC")
    List<ProductEntity> findByBranchIdOrderByStockDesc(@Param("branchId") UUID branchId);
}
