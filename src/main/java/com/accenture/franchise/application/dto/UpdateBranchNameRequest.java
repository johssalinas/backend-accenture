package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar el nombre de una sucursal
 */
@Schema(description = "Datos para actualizar el nombre de una sucursal")
public record UpdateBranchNameRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Nuevo nombre de la sucursal", example = "Sucursal Norte", required = true)
    String name
) {}
