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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controlador REST para Franquicias. */
@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
@Tag(name = "Franquicias", description = "API para gestión de franquicias")
public class FranchiseController {

  private final CreateFranchiseUseCase createFranchiseUseCase;
  private final GetFranchiseUseCase getFranchiseUseCase;
  private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
  private final GetTopStockProductsByFranchiseUseCase getTopStockProductsByFranchiseUseCase;

  /** Crea una nueva franquicia. */
  @PostMapping
  @Operation(summary = "Crear una nueva franquicia")
  @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Franquicia creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "409", description = "Ya existe una franquicia con ese nombre")
      })
  public ResponseEntity<FranchiseResponse> createFranchise(
      @Valid @RequestBody CreateFranchiseRequest request) {
    FranchiseResponse response = createFranchiseUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /** Obtiene una franquicia por su identificador. */
  @GetMapping("/{franchiseId}")
  @Operation(summary = "Obtener una franquicia por ID")
  @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Franquicia encontrada"),
        @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
      })
  public ResponseEntity<FranchiseResponse> getFranchise(@PathVariable UUID franchiseId) {
    FranchiseResponse response = getFranchiseUseCase.execute(franchiseId);
    return ResponseEntity.ok(response);
  }

  /** Actualiza el nombre de una franquicia. */
  @PatchMapping("/{franchiseId}/name")
  @Operation(summary = "Actualizar el nombre de una franquicia")
  @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nombre actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Franquicia no encontrada"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
      })
  public ResponseEntity<FranchiseResponse> updateFranchiseName(
      @PathVariable UUID franchiseId, @Valid @RequestBody UpdateFranchiseNameRequest request) {
    FranchiseResponse response = updateFranchiseNameUseCase.execute(franchiseId, request);
    return ResponseEntity.ok(response);
  }

  /** Obtiene los productos con mayor stock de cada sucursal. */
  @GetMapping("/{franchiseId}/top-stock-products")
  @Operation(summary = "Obtener productos con más stock por sucursal")
  @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
      })
  public ResponseEntity<List<ProductStockResponse>> getTopStockProducts(
      @PathVariable UUID franchiseId) {
    List<ProductStockResponse> response =
        getTopStockProductsByFranchiseUseCase.execute(franchiseId);
    return ResponseEntity.ok(response);
  }
}
