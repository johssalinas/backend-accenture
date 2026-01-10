package com.accenture.franchise.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para Franquicia
 */
@Entity
@Table(name = "franchises")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseEntity extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @OneToMany(
        mappedBy = "franchise",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<BranchEntity> branches = new ArrayList<>();
    
    public void addBranch(BranchEntity branch) {
        branches.add(branch);
        branch.setFranchise(this);
    }
    
    public void removeBranch(BranchEntity branch) {
        branches.remove(branch);
        branch.setFranchise(null);
    }
}
