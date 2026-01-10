package com.accenture.franchise.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA para Sucursal
 */
@Entity
@Table(
    name = "branches",
    uniqueConstraints = @UniqueConstraint(
        name = "branches_unique_name_per_franchise",
        columnNames = {"franchise_id", "name"}
    ),
    indexes = {
        @Index(name = "idx_branches_franchise_id", columnList = "franchise_id"),
        @Index(name = "idx_branches_name", columnList = "name")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity extends BaseEntity {
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private FranchiseEntity franchise;
    
    @OneToMany(
        mappedBy = "branch",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ProductEntity> products = new ArrayList<>();
    
    public void addProduct(ProductEntity product) {
        products.add(product);
        product.setBranch(this);
    }
    
    public void removeProduct(ProductEntity product) {
        products.remove(product);
        product.setBranch(null);
    }
}
