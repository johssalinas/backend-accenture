package com.accenture.franchise.application.dto.mapper;

import com.accenture.franchise.application.dto.*;
import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.model.ProductStock;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Mapper entre modelos de dominio y DTOs
 */
@Component
public class DtoMapper {
    
    public FranchiseResponse toFranchiseResponse(Franchise franchise) {
        if (franchise == null) return null;
        
        return new FranchiseResponse(
            franchise.getId(),
            franchise.getName(),
            franchise.getBranches() != null && !franchise.getBranches().isEmpty()
                ? franchise.getBranches().stream()
                    .map(this::toBranchResponse)
                    .toList()
                : null
        );
    }
    
    public BranchResponse toBranchResponse(Branch branch) {
        if (branch == null) return null;
        
        return new BranchResponse(
            branch.getId(),
            branch.getName(),
            branch.getFranchiseId(),
            branch.getProducts() != null
                ? branch.getProducts().stream()
                    .map(this::toProductResponse)
                    .collect(Collectors.toList())
                : Collections.emptyList()
        );
    }
    
    public ProductResponse toProductResponse(Product product) {
        if (product == null) return null;
        
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getStock(),
            product.getBranchId()
        );
    }
    
    public ProductStockResponse toProductStockResponse(ProductStock productStock) {
        if (productStock == null) return null;
        
        return new ProductStockResponse(
            productStock.productId(),
            productStock.productName(),
            productStock.stock(),
            productStock.branchId(),
            productStock.branchName()
        );
    }
}
