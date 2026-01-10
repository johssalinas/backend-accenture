package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear una nueva franquicia
 */
@Schema(description = "Datos para crear una nueva franquicia")
public record CreateFranchiseRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Nombre de la franquicia", example = "Franquicia ABC", required = true)
    String name
) {}
