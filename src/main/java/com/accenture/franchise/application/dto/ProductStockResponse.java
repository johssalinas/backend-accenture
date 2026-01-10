package com.accenture.franchise.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO de respuesta para productos con más stock por sucursal
 */
@Schema(description = "Información del producto con más stock en una sucursal")
public record ProductStockResponse(
    @Schema(description = "ID del producto", example = "123e4567-e89b-12d3-a456-426614174002")
    UUID productId,
    
    @Schema(description = "Nombre del producto", example = "Laptop Dell")
    String productName,
    
    @Schema(description = "Cantidad en stock", example = "50")
    Integer stock,
    
    @Schema(description = "ID de la sucursal", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID branchId,
    
    @Schema(description = "Nombre de la sucursal", example = "Sucursal Centro")
    String branchName
) {}
