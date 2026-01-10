package com.accenture.franchise.domain.repository;

import com.accenture.franchise.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de salida para persistencia de Productos
 */
public interface ProductRepository {
    
    Product save(Product product);
    
    Optional<Product> findById(UUID id);
    
    List<Product> findByBranchId(UUID branchId);
    
    void deleteById(UUID id);
    
    boolean existsByNameAndBranchId(String name, UUID branchId);
    
    boolean existsById(UUID id);
}
