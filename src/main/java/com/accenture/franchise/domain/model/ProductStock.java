package com.accenture.franchise.domain.model;

import java.util.UUID;

/** Value Object que representa un producto con su stock y la sucursal a la que pertenece. */
public record ProductStock(
    UUID productId, String productName, Integer stock, UUID branchId, String branchName) {}
