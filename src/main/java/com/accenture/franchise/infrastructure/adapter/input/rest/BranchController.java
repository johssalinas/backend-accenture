package com.accenture.franchise.infrastructure.adapter.input.rest;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.CreateBranchRequest;
import com.accenture.franchise.application.dto.UpdateBranchNameRequest;
import com.accenture.franchise.application.usecase.branch.AddBranchToFranchiseUseCase;
import com.accenture.franchise.application.usecase.branch.UpdateBranchNameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controlador REST para Sucursales. */
@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "API para gestión de sucursales")
public class BranchController {

  private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
  private final UpdateBranchNameUseCase updateBranchNameUseCase;

  /** Agrega una nueva sucursal a una franquicia. */
  @PostMapping
  @Operation(summary = "Agregar una nueva sucursal a una franquicia")
  @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sucursal creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Franquicia no encontrada"),
        @ApiResponse(
            responseCode = "409",
            description = "Ya existe una sucursal con ese nombre en la franquicia")
      })
  public ResponseEntity<BranchResponse> addBranch(@Valid @RequestBody CreateBranchRequest request) {
    BranchResponse response = addBranchToFranchiseUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /** Actualiza el nombre de una sucursal. */
  @PatchMapping("/{branchId}/name")
  @Operation(summary = "Actualizar el nombre de una sucursal")
  @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nombre actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
      })
  public ResponseEntity<BranchResponse> updateBranchName(
      @PathVariable UUID branchId, @Valid @RequestBody UpdateBranchNameRequest request) {
    BranchResponse response = updateBranchNameUseCase.execute(branchId, request);
    return ResponseEntity.ok(response);
  }
}
