package com.accenture.franchise.domain.repository;

import com.accenture.franchise.domain.model.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de salida para persistencia de Sucursales
 */
public interface BranchRepository {
    
    Branch save(Branch branch);
    
    Optional<Branch> findById(UUID id);
    
    List<Branch> findByFranchiseId(UUID franchiseId);
    
    void deleteById(UUID id);
    
    boolean existsByNameAndFranchiseId(String name, UUID franchiseId);
    
    boolean existsById(UUID id);
}
