package com.accenture.franchise.application.usecase.branch;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.UpdateBranchNameRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.repository.BranchRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: Actualizar el nombre de una sucursal. */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateBranchNameUseCase {

  private final BranchRepository branchRepository;
  private final DtoMapper mapper;

  /** Ejecuta la actualización del nombre de una sucursal. */
  public BranchResponse execute(UUID branchId, UpdateBranchNameRequest request) {
    log.info("Updating branch name with id: {}", branchId);

    Branch branch =
        branchRepository
            .findById(branchId)
            .orElseThrow(() -> new EntityNotFoundException("Branch", branchId));

    // Actualizar usando lógica de dominio
    branch.updateName(request.name());

    // Persistir
    Branch updatedBranch = branchRepository.save(branch);

    log.info("Branch name updated successfully");

    return mapper.toBranchResponse(updatedBranch);
  }
}
