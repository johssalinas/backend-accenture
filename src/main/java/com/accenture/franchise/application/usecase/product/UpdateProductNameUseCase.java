package com.accenture.franchise.application.usecase.product;

import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.UpdateProductNameRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: Actualizar el nombre de un producto. */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateProductNameUseCase {

  private final ProductRepository productRepository;
  private final DtoMapper mapper;

  /** Ejecuta la actualización del nombre de un producto. */
  public ProductResponse execute(UUID productId, UpdateProductNameRequest request) {
    log.info("Updating product name with id: {}", productId);

    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product", productId));

    // Actualizar usando lógica de dominio
    product.updateName(request.name());

    // Persistir
    Product updatedProduct = productRepository.save(product);

    log.info("Product name updated successfully");

    return mapper.toProductResponse(updatedProduct);
  }
}
