package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta para Sucursal
 */
@Schema(description = "Información de una sucursal")
public record BranchResponse(
    @Schema(description = "ID único de la sucursal", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "Nombre de la sucursal", example = "Sucursal Centro")
    String name,
    
    @Schema(description = "ID de la franquicia a la que pertenece", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID franchiseId,
    
    @Schema(description = "Lista de productos de la sucursal")
    List<ProductResponse> products
) {}
