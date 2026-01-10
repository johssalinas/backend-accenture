package com.accenture.franchise.application.usecase.product;

import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.UpdateProductStockRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Actualizar el stock de un producto
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateProductStockUseCase {
    
    private final ProductRepository productRepository;
    private final DtoMapper mapper;
    
    public ProductResponse execute(UUID productId, UpdateProductStockRequest request) {
        log.info("Updating product stock with id: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        
        // Actualizar usando lógica de dominio
        product.updateStock(request.stock());
        
        // Persistir
        Product updatedProduct = productRepository.save(product);
        
        log.info("Product stock updated successfully");
        
        return mapper.toProductResponse(updatedProduct);
    }
}
