package com.accenture.franchise.infrastructure.adapter.output.persistence.repository;

import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para Franquicias
 */
@Repository
public interface FranchiseJpaRepository extends JpaRepository<FranchiseEntity, UUID> {
    
    boolean existsByName(String name);
    
    @Query("SELECT f FROM FranchiseEntity f LEFT JOIN FETCH f.branches b LEFT JOIN FETCH b.products WHERE f.id = :id")
    Optional<FranchiseEntity> findByIdWithBranchesAndProducts(@Param("id") UUID id);
}
