package com.accenture.franchise.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/** DTO de respuesta para Franquicia. */
@Schema(description = "Información de una franquicia")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FranchiseResponse(
    @Schema(
            description = "ID único de la franquicia",
            example = "123e4567-e89b-12d3-a456-426614174001")
        UUID id,
    @Schema(description = "Nombre de la franquicia", example = "Franquicia ABC") String name,
    @Schema(description = "Lista de sucursales de la franquicia") List<BranchResponse> branches) {}
