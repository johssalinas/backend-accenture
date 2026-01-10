package com.accenture.franchise.infrastructure.adapter.output.persistence;

import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.repository.ProductRepository;
import com.accenture.franchise.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.accenture.franchise.infrastructure.adapter.output.persistence.repository.ProductJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/** Adapter de persistencia para Productos. */
@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository {

  private final ProductJpaRepository jpaRepository;
  private final ProductPersistenceMapper mapper;

  @Override
  @CacheEvict(
      value = {"products", "branches", "franchises"},
      allEntries = true)
  public Product save(Product product) {
    var entity = mapper.toEntity(product);
    var savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Cacheable(value = "products", key = "#id")
  public Optional<Product> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Product> findByBranchId(UUID branchId) {
    return jpaRepository.findByBranchId(branchId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  @CacheEvict(
      value = {"products", "branches", "franchises"},
      allEntries = true)
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public boolean existsByNameAndBranchId(String name, UUID branchId) {
    return jpaRepository.existsByNameAndBranchId(name, branchId);
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }
}
