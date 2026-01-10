package com.accenture.franchise.application.usecase.branch;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.CreateBranchRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.repository.BranchRepository;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Agregar una nueva sucursal a una franquicia
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddBranchToFranchiseUseCase {
    
    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final DtoMapper mapper;
    
    public BranchResponse execute(CreateBranchRequest request) {
        log.info("Adding branch to franchise: {}", request.franchiseId());
        
        // Validar que la franquicia existe
        if (!franchiseRepository.existsById(request.franchiseId())) {
            throw new EntityNotFoundException("Franchise", request.franchiseId());
        }
        
        // Nombre único por franquicia
        if (branchRepository.existsByNameAndFranchiseId(request.name(), request.franchiseId())) {
            throw new BusinessRuleViolationException(
                "Branch with name already exists in this franchise: " + request.name()
            );
        }
        
        // Crear entidad de dominio
        Branch branch = Branch.builder()
            .name(request.name())
            .franchiseId(request.franchiseId())
            .build();
        
        // Persistir
        Branch savedBranch = branchRepository.save(branch);
        
        log.info("Branch added successfully with id: {}", savedBranch.getId());
        
        return mapper.toBranchResponse(savedBranch);
    }
}
