package com.accenture.franchise.infrastructure.adapter.output.persistence.mapper;

import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper entre Franchise (dominio) y FranchiseEntity (JPA)
 */
@Component
public class FranchisePersistenceMapper {
    
    private final BranchPersistenceMapper branchMapper;
    
    public FranchisePersistenceMapper(BranchPersistenceMapper branchMapper) {
        this.branchMapper = branchMapper;
    }
    
    public Franchise toDomain(FranchiseEntity entity) {
        if (entity == null) return null;
        
        return Franchise.builder()
            .id(entity.getId())
            .name(entity.getName())
            .branches(
                entity.getBranches() != null
                    ? entity.getBranches().stream()
                        .map(branchMapper::toDomain)
                        .collect(Collectors.toList())
                    : null
            )
            .build();
    }
    
    public FranchiseEntity toEntity(Franchise domain) {
        if (domain == null) return null;
        
        FranchiseEntity entity = FranchiseEntity.builder()
            .name(domain.getName())
            .build();
        
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        
        if (domain.getBranches() != null) {
            domain.getBranches().forEach(branch -> {
                entity.addBranch(branchMapper.toEntity(branch));
            });
        }
        
        return entity;
    }
}
