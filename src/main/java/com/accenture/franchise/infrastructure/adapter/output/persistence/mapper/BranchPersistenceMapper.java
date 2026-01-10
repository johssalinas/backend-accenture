package com.accenture.franchise.infrastructure.adapter.output.persistence.mapper;

import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.repository.FranchiseJpaRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper entre Branch (dominio) y BranchEntity (JPA)
 */
@Component
public class BranchPersistenceMapper {
    
    private final ProductPersistenceMapper productMapper;
    private final FranchiseJpaRepository franchiseJpaRepository;
    
    public BranchPersistenceMapper(ProductPersistenceMapper productMapper, FranchiseJpaRepository franchiseJpaRepository) {
        this.productMapper = productMapper;
        this.franchiseJpaRepository = franchiseJpaRepository;
    }
    
    public Branch toDomain(BranchEntity entity) {
        if (entity == null) return null;
        
        return Branch.builder()
            .id(entity.getId())
            .name(entity.getName())
            .franchiseId(entity.getFranchise() != null ? entity.getFranchise().getId() : null)
            .products(
                entity.getProducts() != null
                    ? entity.getProducts().stream()
                        .map(productMapper::toDomain)
                        .collect(Collectors.toList())
                    : null
            )
            .build();
    }
    
    public BranchEntity toEntity(Branch domain) {
        if (domain == null) return null;
        
        BranchEntity entity = BranchEntity.builder()
            .name(domain.getName())
            .build();
        
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        
        // Establecer la relación con FranchiseEntity (requerido por JPA)
        if (domain.getFranchiseId() != null) {
            FranchiseEntity franchise = franchiseJpaRepository.getReferenceById(domain.getFranchiseId());
            entity.setFranchise(franchise);
        }
        
        if (domain.getProducts() != null) {
            domain.getProducts().forEach(product -> {
                entity.addProduct(productMapper.toEntity(product));
            });
        }
        
        return entity;
    }
}
