package com.accenture.franchise.infrastructure.adapter.output.persistence.mapper;

import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.ProductEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.repository.BranchJpaRepository;
import org.springframework.stereotype.Component;

/** Mapper entre ProductEntity (JPA) y Product (dominio). */
@Component
public class ProductPersistenceMapper {

  private final BranchJpaRepository branchJpaRepository;

  /** Crea el mapper con la dependencia del repositorio de sucursales. */
  public ProductPersistenceMapper(BranchJpaRepository branchJpaRepository) {
    this.branchJpaRepository = branchJpaRepository;
  }

  /** Convierte una entidad JPA de producto al modelo de dominio. */
  public Product toDomain(ProductEntity entity) {
    if (entity == null) {
      return null;
    }

    return Product.builder()
        .id(entity.getId())
        .name(entity.getName())
        .stock(entity.getStock())
        .branchId(entity.getBranch() != null ? entity.getBranch().getId() : null)
        .build();
  }

  /** Convierte un modelo de dominio de producto a entidad JPA. */
  public ProductEntity toEntity(Product domain) {
    if (domain == null) {
      return null;
    }

    ProductEntity entity =
        ProductEntity.builder().name(domain.getName()).stock(domain.getStock()).build();

    if (domain.getId() != null) {
      entity.setId(domain.getId());
    }

    // Establecer la relación con BranchEntity (requerido por JPA)
    if (domain.getBranchId() != null) {
      BranchEntity branch = branchJpaRepository.getReferenceById(domain.getBranchId());
      entity.setBranch(branch);
    }

    return entity;
  }
}
