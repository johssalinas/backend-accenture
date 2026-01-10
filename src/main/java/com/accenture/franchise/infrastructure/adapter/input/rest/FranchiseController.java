package com.accenture.franchise.infrastructure.adapter.input.rest;

import com.accenture.franchise.application.dto.CreateFranchiseRequest;
import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.ProductStockResponse;
import com.accenture.franchise.application.dto.UpdateFranchiseNameRequest;
import com.accenture.franchise.application.usecase.franchise.CreateFranchiseUseCase;
import com.accenture.franchise.application.usecase.franchise.GetFranchiseUseCase;
import com.accenture.franchise.application.usecase.franchise.GetTopStockProductsByFranchiseUseCase;
import com.accenture.franchise.application.usecase.franchise.UpdateFranchiseNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para Franquicias
 */
@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
@Tag(name = "Franchises", description = "API para gestión de franquicias")
public class FranchiseController {
    
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final GetFranchiseUseCase getFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final GetTopStockProductsByFranchiseUseCase getTopStockProductsByFranchiseUseCase;
    
    @PostMapping
    @Operation(summary = "Crear una nueva franquicia")
    public ResponseEntity<FranchiseResponse> createFranchise(
            @Valid @RequestBody CreateFranchiseRequest request) {
        FranchiseResponse response = createFranchiseUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{franchiseId}")
    @Operation(summary = "Obtener una franquicia por ID")
    public ResponseEntity<FranchiseResponse> getFranchise(
            @PathVariable UUID franchiseId) {
        FranchiseResponse response = getFranchiseUseCase.execute(franchiseId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{franchiseId}/name")
    @Operation(summary = "Actualizar el nombre de una franquicia")
    public ResponseEntity<FranchiseResponse> updateFranchiseName(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody UpdateFranchiseNameRequest request) {
        FranchiseResponse response = updateFranchiseNameUseCase.execute(franchiseId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{franchiseId}/top-stock-products")
    @Operation(summary = "Obtener productos con más stock por sucursal")
    public ResponseEntity<List<ProductStockResponse>> getTopStockProducts(
            @PathVariable UUID franchiseId) {
        List<ProductStockResponse> response = getTopStockProductsByFranchiseUseCase.execute(franchiseId);
        return ResponseEntity.ok(response);
    }
}
