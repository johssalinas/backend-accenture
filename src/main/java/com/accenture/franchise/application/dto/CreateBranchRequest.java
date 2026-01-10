package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO para crear una nueva sucursal
 */
@Schema(description = "Datos para crear una nueva sucursal")
public record CreateBranchRequest(
    @NotNull(message = "Franchise ID is required")
    @Schema(description = "ID de la franquicia", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    UUID franchiseId,
    
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Nombre de la sucursal", example = "Sucursal Centro", required = true)
    String name
) {}
