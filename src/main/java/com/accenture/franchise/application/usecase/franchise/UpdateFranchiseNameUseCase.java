package com.accenture.franchise.application.usecase.franchise;

import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.UpdateFranchiseNameRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Actualizar el nombre de una franquicia
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateFranchiseNameUseCase {
    
    private final FranchiseRepository franchiseRepository;
    private final DtoMapper mapper;
    
    public FranchiseResponse execute(UUID franchiseId, UpdateFranchiseNameRequest request) {
        log.info("Updating franchise name with id: {}", franchiseId);
        
        Franchise franchise = franchiseRepository.findById(franchiseId)
            .orElseThrow(() -> new EntityNotFoundException("Franchise", franchiseId));
        
        // Actualizar usando lógica de dominio
        franchise.updateName(request.name());
        
        // Persistir
        Franchise updatedFranchise = franchiseRepository.save(franchise);
        
        log.info("Franchise name updated successfully");
        
        return mapper.toFranchiseResponse(updatedFranchise);
    }
}
