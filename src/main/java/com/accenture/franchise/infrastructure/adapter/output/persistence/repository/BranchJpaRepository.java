package com.accenture.franchise.infrastructure.adapter.output.persistence.repository;

import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para Sucursales
 */
@Repository
public interface BranchJpaRepository extends JpaRepository<BranchEntity, UUID> {
    
    List<BranchEntity> findByFranchiseId(UUID franchiseId);
    
    boolean existsByNameAndFranchiseId(String name, UUID franchiseId);
    
    @Query("SELECT b FROM BranchEntity b LEFT JOIN FETCH b.products WHERE b.id = :id")
    Optional<BranchEntity> findByIdWithProducts(@Param("id") UUID id);
}
