package com.accenture.franchise.infrastructure.adapter.output.persistence;

import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import com.accenture.franchise.infrastructure.adapter.output.persistence.mapper.FranchisePersistenceMapper;
import com.accenture.franchise.infrastructure.adapter.output.persistence.repository.FranchiseJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/** Adapter de persistencia para Franquicias. */
@Component
@RequiredArgsConstructor
public class FranchisePersistenceAdapter implements FranchiseRepository {

  private final FranchiseJpaRepository jpaRepository;
  private final FranchisePersistenceMapper mapper;

  @Override
  @CacheEvict(value = "franchises", allEntries = true)
  public Franchise save(Franchise franchise) {
    var entity = mapper.toEntity(franchise);
    var savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Cacheable(value = "franchises", key = "#id")
  public Optional<Franchise> findById(UUID id) {
    return jpaRepository.findByIdWithBranchesAndProducts(id).map(mapper::toDomain);
  }

  @Override
  @Cacheable(value = "franchises", key = "'all'")
  public List<Franchise> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  @CacheEvict(value = "franchises", allEntries = true)
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public boolean existsByName(String name) {
    return jpaRepository.existsByName(name);
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }
}
