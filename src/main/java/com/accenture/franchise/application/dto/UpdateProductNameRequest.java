package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar el nombre de un producto
 */
@Schema(description = "Datos para actualizar el nombre de un producto")
public record UpdateProductNameRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Nuevo nombre del producto", example = "Laptop HP", requiredMode = Schema.RequiredMode.REQUIRED)
    String name
) {}
