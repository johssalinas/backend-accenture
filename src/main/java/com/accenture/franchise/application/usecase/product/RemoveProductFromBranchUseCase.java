package com.accenture.franchise.application.usecase.product;

import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Eliminar un producto de una sucursal
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RemoveProductFromBranchUseCase {
    
    private final ProductRepository productRepository;
    
    public void execute(UUID productId) {
        log.info("Removing product with id: {}", productId);
        
        // Validar que el producto existe
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product", productId);
        }
        
        // Eliminar
        productRepository.deleteById(productId);
        
        log.info("Product removed successfully");
    }
}
