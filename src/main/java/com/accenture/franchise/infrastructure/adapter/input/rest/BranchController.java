package com.accenture.franchise.infrastructure.adapter.input.rest;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.CreateBranchRequest;
import com.accenture.franchise.application.dto.UpdateBranchNameRequest;
import com.accenture.franchise.application.usecase.branch.AddBranchToFranchiseUseCase;
import com.accenture.franchise.application.usecase.branch.UpdateBranchNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para Sucursales
 */
@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "API para gestión de sucursales")
public class BranchController {
    
    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    
    @PostMapping
    @Operation(summary = "Agregar una nueva sucursal a una franquicia")
    public ResponseEntity<BranchResponse> addBranch(
            @Valid @RequestBody CreateBranchRequest request) {
        BranchResponse response = addBranchToFranchiseUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PatchMapping("/{branchId}/name")
    @Operation(summary = "Actualizar el nombre de una sucursal")
    public ResponseEntity<BranchResponse> updateBranchName(
            @PathVariable UUID branchId,
            @Valid @RequestBody UpdateBranchNameRequest request) {
        BranchResponse response = updateBranchNameUseCase.execute(branchId, request);
        return ResponseEntity.ok(response);
    }
}
