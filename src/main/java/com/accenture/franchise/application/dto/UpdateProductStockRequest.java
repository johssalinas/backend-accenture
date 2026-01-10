package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el stock de un producto
 */
@Schema(description = "Datos para actualizar el stock de un producto")
public record UpdateProductStockRequest(
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    @Schema(description = "Nueva cantidad en stock", example = "100", required = true, minimum = "0")
    Integer stock
) {}
