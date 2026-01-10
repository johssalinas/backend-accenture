package com.accenture.franchise.application.usecase.franchise;

import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: Obtener una franquicia por ID. */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetFranchiseUseCase {

  private final FranchiseRepository franchiseRepository;
  private final DtoMapper mapper;

  /** Obtiene una franquicia por su identificador. */
  public FranchiseResponse execute(UUID franchiseId) {
    log.info("Getting franchise with id: {}", franchiseId);
    Franchise franchise =
        franchiseRepository
            .findById(franchiseId)
            .orElseThrow(() -> new EntityNotFoundException("Franchise", franchiseId));

    return mapper.toFranchiseResponse(franchise);
  }
}
