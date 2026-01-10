package com.accenture.franchise.infrastructure.adapter.output.persistence;

import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.repository.BranchRepository;
import com.accenture.franchise.infrastructure.adapter.output.persistence.mapper.BranchPersistenceMapper;
import com.accenture.franchise.infrastructure.adapter.output.persistence.repository.BranchJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter de persistencia para Sucursales
 */
@Component
@RequiredArgsConstructor
public class BranchPersistenceAdapter implements BranchRepository {
    
    private final BranchJpaRepository jpaRepository;
    private final BranchPersistenceMapper mapper;
    
    @Override
    @CacheEvict(value = {"branches", "franchises"}, allEntries = true)
    public Branch save(Branch branch) {
        var entity = mapper.toEntity(branch);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Cacheable(value = "branches", key = "#id")
    public Optional<Branch> findById(UUID id) {
        return jpaRepository.findByIdWithProducts(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Branch> findByFranchiseId(UUID franchiseId) {
        return jpaRepository.findByFranchiseId(franchiseId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @CacheEvict(value = {"branches", "franchises"}, allEntries = true)
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByNameAndFranchiseId(String name, UUID franchiseId) {
        return jpaRepository.existsByNameAndFranchiseId(name, franchiseId);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
