package com.accenture.franchise.domain.repository;

import com.accenture.franchise.domain.model.Franchise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de salida para persistencia de Franquicias
 */
public interface FranchiseRepository {
    
    Franchise save(Franchise franchise);
    
    Optional<Franchise> findById(UUID id);
    
    List<Franchise> findAll();
    
    void deleteById(UUID id);
    
    boolean existsByName(String name);
    
    boolean existsById(UUID id);
}
