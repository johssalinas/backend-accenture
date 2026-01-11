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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controlador REST para Productos. */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos")
public class ProductController {

  private final AddProductToBranchUseCase addProductToBranchUseCase;
  private final RemoveProductFromBranchUseCase removeProductFromBranchUseCase;
  private final UpdateProductStockUseCase updateProductStockUseCase;
  private final UpdateProductNameUseCase updateProductNameUseCase;

  /** Agrega un nuevo producto a una sucursal. */
  @PostMapping
  @Operation(summary = "Agregar un nuevo producto a una sucursal")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
    @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe un producto con ese nombre en la sucursal")
  })
  public ResponseEntity<ProductResponse> addProduct(
      @Valid @RequestBody CreateProductRequest request) {
    ProductResponse response = addProductToBranchUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /** Elimina un producto de una sucursal. */
  @DeleteMapping("/{productId}")
  @Operation(summary = "Eliminar un producto de una sucursal")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
  })
  public ResponseEntity<Void> removeProduct(@PathVariable UUID productId) {
    removeProductFromBranchUseCase.execute(productId);
    return ResponseEntity.noContent().build();
  }

  /** Actualiza el stock de un producto. */
  @PatchMapping("/{productId}/stock")
  @Operation(summary = "Actualizar el stock de un producto")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
  })
  public ResponseEntity<ProductResponse> updateProductStock(
      @PathVariable UUID productId, @Valid @RequestBody UpdateProductStockRequest request) {
    ProductResponse response = updateProductStockUseCase.execute(productId, request);
    return ResponseEntity.ok(response);
  }

  /** Actualiza el nombre de un producto. */
  @PatchMapping("/{productId}/name")
  @Operation(summary = "Actualizar el nombre de un producto")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Nombre actualizado exitosamente"),
    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
  })
  public ResponseEntity<ProductResponse> updateProductName(
      @PathVariable UUID productId, @Valid @RequestBody UpdateProductNameRequest request) {
    ProductResponse response = updateProductNameUseCase.execute(productId, request);
    return ResponseEntity.ok(response);
  }
}
