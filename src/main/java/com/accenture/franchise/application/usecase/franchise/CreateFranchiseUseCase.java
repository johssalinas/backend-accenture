package com.accenture.franchise.application.usecase.franchise;

import com.accenture.franchise.application.dto.CreateFranchiseRequest;
import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

/**
 * Caso de uso: Crear una nueva franquicia
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateFranchiseUseCase {
    
    private final FranchiseRepository franchiseRepository;
    private final DtoMapper mapper;
    
    public FranchiseResponse execute(CreateFranchiseRequest request) {
        log.info("Creating franchise with name: {}", request.name());
        
        // Validar regla de negocio: nombre único
        if (franchiseRepository.existsByName(request.name())) {
            throw new BusinessRuleViolationException(
                "Franchise with name already exists: " + request.name()
            );
        }
        
        // Crear entidad de dominio
        Franchise franchise = Franchise.builder()
            .name(request.name())
            .branches(new ArrayList<>())
            .build();
        
        // Persistir
        Franchise savedFranchise = franchiseRepository.save(franchise);
        
        log.info("Franchise created successfully with id: {}", savedFranchise.getId());
        
        return mapper.toFranchiseResponse(savedFranchise);
    }
}
