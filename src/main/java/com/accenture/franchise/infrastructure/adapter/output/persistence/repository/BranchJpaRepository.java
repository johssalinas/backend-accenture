package com.accenture.franchise.infrastructure.adapter.output.persistence.repository;

import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repositorio JPA para sucursales. */
@Repository
public interface BranchJpaRepository extends JpaRepository<BranchEntity, UUID> {

  /** Obtiene todas las sucursales de una franquicia. */
  List<BranchEntity> findByFranchiseId(UUID franchiseId);

  /** Verifica si existe una sucursal con nombre en una franquicia. */
  boolean existsByNameAndFranchiseId(String name, UUID franchiseId);

  /** Busca una sucursal por ID con sus productos cargados. */
  @Query("SELECT b FROM BranchEntity b LEFT JOIN FETCH b.products WHERE b.id = :id")
  Optional<BranchEntity> findByIdWithProducts(@Param("id") UUID id);
}
