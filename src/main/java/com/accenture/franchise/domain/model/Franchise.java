package com.accenture.franchise.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Franquicia - Entidad raíz del agregado
 * Gestiona una lista de sucursales
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Franchise {
    
    private UUID id;
    private String name;
    
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
    
    /**
     * Agrega una nueva sucursal a la franquicia
     */
    public void addBranch(Branch branch) {
        if (branch == null) {
            throw new IllegalArgumentException("Branch cannot be null");
        }
        if (branches.stream().anyMatch(b -> b.getName().equals(branch.getName()))) {
            throw new IllegalArgumentException("Branch name already exists in this franchise");
        }
        branches.add(branch);
    }
    
    /**
     * Actualiza el nombre de la franquicia
     */
    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Franchise name cannot be blank");
        }
        this.name = newName.trim();
    }
    
    /**
     * Obtiene el producto con más stock por cada sucursal
     */
    public List<ProductStock> getTopStockProductsByBranch() {
        return branches.stream()
            .map(branch -> branch.getProductWithMostStock())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
    
    /**
     * Encuentra una sucursal por ID
     */
    public Optional<Branch> findBranchById(UUID branchId) {
        return branches.stream()
            .filter(b -> b.getId().equals(branchId))
            .findFirst();
    }
}
