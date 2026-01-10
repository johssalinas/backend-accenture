package com.accenture.franchise.infrastructure.adapter.output.persistence.mapper;

import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Mapper entre FranchiseEntity (JPA) y Franchise (dominio). */
@Component
public class FranchisePersistenceMapper {

  private final BranchPersistenceMapper branchMapper;

  /** Crea el mapper con la dependencia del mapper de sucursales. */
  public FranchisePersistenceMapper(BranchPersistenceMapper branchMapper) {
    this.branchMapper = branchMapper;
  }

  /** Convierte una entidad JPA de franquicia al modelo de dominio. */
  public Franchise toDomain(FranchiseEntity entity) {
    if (entity == null) {
      return null;
    }

    return Franchise.builder()
        .id(entity.getId())
        .name(entity.getName())
        .branches(
            entity.getBranches() != null
                ? entity.getBranches().stream()
                    .map(branchMapper::toDomain)
                    .collect(Collectors.toList())
                : null)
        .build();
  }

  /** Convierte un modelo de dominio de franquicia a entidad JPA. */
  public FranchiseEntity toEntity(Franchise domain) {
    if (domain == null) {
      return null;
    }

    FranchiseEntity entity = FranchiseEntity.builder().name(domain.getName()).build();

    if (domain.getId() != null) {
      entity.setId(domain.getId());
    }

    if (domain.getBranches() != null) {
      domain.getBranches().forEach(branch -> entity.addBranch(branchMapper.toEntity(branch)));
    }

    return entity;
  }
}
