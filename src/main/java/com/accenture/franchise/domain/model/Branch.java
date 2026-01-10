package com.accenture.franchise.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Sucursal - Entidad que pertenece a una Franquicia
 * Gestiona un listado de productos
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    
    private UUID id;
    private String name;
    private UUID franchiseId;
    
    @Builder.Default
    private List<Product> products = new ArrayList<>();
    
    /**
     * Agrega un nuevo producto a la sucursal
     */
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (products.stream().anyMatch(p -> p.getName().equals(product.getName()))) {
            throw new IllegalArgumentException("Product name already exists in this branch");
        }
        product.setBranchId(this.id);
        products.add(product);
    }
    
    /**
     * Elimina un producto de la sucursal
     */
    public void removeProduct(UUID productId) {
        products.removeIf(p -> p.getId().equals(productId));
    }
    
    /**
     * Actualiza el nombre de la sucursal
     */
    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Branch name cannot be blank");
        }
        this.name = newName.trim();
    }
    
    /**
     * Obtiene el producto con mayor stock en esta sucursal
     */
    public Optional<ProductStock> getProductWithMostStock() {
        return products.stream()
            .max(Comparator.comparing(Product::getStock))
            .map(product -> new ProductStock(
                product.getId(),
                product.getName(),
                product.getStock(),
                this.id,
                this.name
            ));
    }
    
    /**
     * Encuentra un producto por ID
     */
    public Optional<Product> findProductById(UUID productId) {
        return products.stream()
            .filter(p -> p.getId().equals(productId))
            .findFirst();
    }
}
