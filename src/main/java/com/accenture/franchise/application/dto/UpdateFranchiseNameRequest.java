package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO para actualizar el nombre de una franquicia. */
@Schema(description = "Datos para actualizar el nombre de una franquicia")
public record UpdateFranchiseNameRequest(
    @NotBlank(message = "Name is required")
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        @Schema(
            description = "Nuevo nombre de la franquicia",
            example = "Franquicia XYZ",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String name) {}
