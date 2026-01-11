package com.accenture.franchise.infrastructure.adapter.output.persistence.repository;

import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repositorio JPA para franquicias. */
@Repository
public interface FranchiseJpaRepository extends JpaRepository<FranchiseEntity, UUID> {

  /** Verifica si existe una franquicia con el nombre indicado. */
  boolean existsByName(String name);

  /** Busca una franquicia por ID con sus sucursales y productos cargados. */
  @Query(
      "SELECT f FROM FranchiseEntity f "
          + "LEFT JOIN FETCH f.branches b LEFT JOIN FETCH b.products "
          + "WHERE f.id = :id")
  Optional<FranchiseEntity> findByIdWithBranchesAndProducts(@Param("id") UUID id);
}
