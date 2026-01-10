package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO de respuesta para Producto
 */
@Schema(description = "Información de un producto")
public record ProductResponse(
    @Schema(description = "ID único del producto", example = "123e4567-e89b-12d3-a456-426614174002")
    UUID id,
    
    @Schema(description = "Nombre del producto", example = "Laptop Dell")
    String name,
    
    @Schema(description = "Cantidad en stock", example = "50")
    Integer stock,
    
    @Schema(description = "ID de la sucursal a la que pertenece", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID branchId
) {}
