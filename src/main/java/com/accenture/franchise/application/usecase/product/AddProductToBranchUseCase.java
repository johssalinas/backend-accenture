package com.accenture.franchise.application.usecase.product;

import com.accenture.franchise.application.dto.CreateProductRequest;
import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.repository.BranchRepository;
import com.accenture.franchise.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: Agregar un nuevo producto a una sucursal. */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddProductToBranchUseCase {

  private final BranchRepository branchRepository;
  private final ProductRepository productRepository;
  private final DtoMapper mapper;

  /** Ejecuta la adición de un producto a una sucursal. */
  public ProductResponse execute(CreateProductRequest request) {
    log.info("Adding product to branch: {}", request.branchId());

    // Validar que la sucursal existe
    if (!branchRepository.existsById(request.branchId())) {
      throw new EntityNotFoundException("Branch", request.branchId());
    }

    // Validar regla de negocio: nombre único por sucursal
    if (productRepository.existsByNameAndBranchId(request.name(), request.branchId())) {
      throw new BusinessRuleViolationException(
          "Product with name already exists in this branch: " + request.name());
    }

    // Crear entidad de dominio
    Product product =
        Product.builder()
            .name(request.name())
            .stock(request.stock())
            .branchId(request.branchId())
            .build();

    // Persistir
    Product savedProduct = productRepository.save(product);

    log.info("Product added successfully with id: {}", savedProduct.getId());

    return mapper.toProductResponse(savedProduct);
  }
}
