package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO para crear un nuevo producto
 */
@Schema(description = "Datos para crear un nuevo producto")
public record CreateProductRequest(
    @NotNull(message = "Branch ID is required")
    @Schema(description = "ID de la sucursal", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    UUID branchId,
    
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Nombre del producto", example = "Laptop Dell", requiredMode = Schema.RequiredMode.REQUIRED)
    String name,
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    @Schema(description = "Cantidad inicial en stock", example = "50", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    Integer stock
) {}
