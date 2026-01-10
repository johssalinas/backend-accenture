package com.accenture.franchise.application.usecase.franchise;

import com.accenture.franchise.application.dto.ProductStockResponse;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Caso de uso: Obtener productos con más stock por sucursal de una franquicia
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetTopStockProductsByFranchiseUseCase {
    
    private final FranchiseRepository franchiseRepository;
    private final DtoMapper mapper;
    
    public List<ProductStockResponse> execute(UUID franchiseId) {
        log.info("Getting top stock products for franchise: {}", franchiseId);
        
        Franchise franchise = franchiseRepository.findById(franchiseId)
            .orElseThrow(() -> new EntityNotFoundException("Franchise", franchiseId));
        
        return franchise.getTopStockProductsByBranch().stream()
            .map(mapper::toProductStockResponse)
            .collect(Collectors.toList());
    }
}
