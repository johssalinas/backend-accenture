package com.accenture.franchise.infrastructure.adapter.input.rest;

import com.accenture.franchise.application.dto.CreateProductRequest;
import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.UpdateProductNameRequest;
import com.accenture.franchise.application.dto.UpdateProductStockRequest;
import com.accenture.franchise.application.usecase.product.AddProductToBranchUseCase;
import com.accenture.franchise.application.usecase.product.RemoveProductFromBranchUseCase;
import com.accenture.franchise.application.usecase.product.UpdateProductNameUseCase;
import com.accenture.franchise.application.usecase.product.UpdateProductStockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para Productos
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API para gestión de productos")
public class ProductController {
    
    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final RemoveProductFromBranchUseCase removeProductFromBranchUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    
    @PostMapping
    @Operation(summary = "Agregar un nuevo producto a una sucursal")
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = addProductToBranchUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/{productId}")
    @Operation(summary = "Eliminar un producto de una sucursal")
    public ResponseEntity<Void> removeProduct(
            @PathVariable UUID productId) {
        removeProductFromBranchUseCase.execute(productId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{productId}/stock")
    @Operation(summary = "Actualizar el stock de un producto")
    public ResponseEntity<ProductResponse> updateProductStock(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductStockRequest request) {
        ProductResponse response = updateProductStockUseCase.execute(productId, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{productId}/name")
    @Operation(summary = "Actualizar el nombre de un producto")
    public ResponseEntity<ProductResponse> updateProductName(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductNameRequest request) {
        ProductResponse response = updateProductNameUseCase.execute(productId, request);
        return ResponseEntity.ok(response);
    }
}
