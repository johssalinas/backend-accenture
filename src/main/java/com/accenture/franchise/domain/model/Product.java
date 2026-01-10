package com.accenture.franchise.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Producto - Entidad que pertenece a una Sucursal
 * Representa un producto con nombre y cantidad de stock
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private UUID id;
    private String name;
    private Integer stock;
    
    @Setter
    private UUID branchId;
    
    /**
     * Actualiza el stock del producto
     */
    public void updateStock(Integer newStock) {
        if (newStock == null || newStock < 0) {
            throw new IllegalArgumentException("Stock must be a positive number");
        }
        this.stock = newStock;
    }
    
    /**
     * Actualiza el nombre del producto
     */
    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        this.name = newName.trim();
    }
}
