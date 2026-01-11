package com.accenture.franchise.application.usecase.branch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.UpdateBranchNameRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.repository.BranchRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link UpdateBranchNameUseCase}.
 *
 * <p>Siguiendo mejores prácticas:
 *
 * <ul>
 *   <li>Uso de Mockito para mocks y JUnit 5 para assertions
 *   <li>Patrón AAA (Arrange-Act-Assert)
 *   <li>Tests independientes y aislados
 *   <li>Uso de BDDMockito para mejorar legibilidad
 *   <li>Tests organizados con @Nested para agrupar casos relacionados
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateBranchNameUseCase - Pruebas Unitarias")
class UpdateBranchNameUseCaseTest {

  @Mock private BranchRepository branchRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private UpdateBranchNameUseCase updateBranchNameUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe actualizar nombre de sucursal correctamente")
    void shouldUpdateBranchNameSuccessfully() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      String oldName = "Sucursal Centro";
      String newName = "Sucursal Norte";

      UpdateBranchNameRequest request = new UpdateBranchNameRequest(newName);

      Branch existingBranch =
          Branch.builder()
              .id(branchId)
              .name(oldName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      Branch updatedBranch =
          Branch.builder()
              .id(branchId)
              .name(newName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(branchId, newName, franchiseId, new ArrayList<>());

      given(branchRepository.findById(branchId)).willReturn(Optional.of(existingBranch));
      given(branchRepository.save(any(Branch.class))).willReturn(updatedBranch);
      given(mapper.toBranchResponse(updatedBranch)).willReturn(expectedResponse);

      // Act
      BranchResponse result = updateBranchNameUseCase.execute(branchId, request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(branchId);
      assertThat(result.name()).isEqualTo(newName);
      assertThat(result.franchiseId()).isEqualTo(franchiseId);

      verify(branchRepository).findById(branchId);
      verify(branchRepository).save(any(Branch.class));
      verify(mapper).toBranchResponse(updatedBranch);
    }

    @Test
    @DisplayName("Debe hacer trim del nombre actualizado")
    void shouldTrimUpdatedName() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      String nameWithSpaces = "  Sucursal Sur  ";
      String trimmedName = "Sucursal Sur";

      UpdateBranchNameRequest request = new UpdateBranchNameRequest(nameWithSpaces);

      Branch existingBranch =
          Branch.builder()
              .id(branchId)
              .name("Old Name")
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      Branch updatedBranch =
          Branch.builder()
              .id(branchId)
              .name(trimmedName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(branchId, trimmedName, franchiseId, new ArrayList<>());

      given(branchRepository.findById(branchId)).willReturn(Optional.of(existingBranch));
      given(branchRepository.save(any(Branch.class))).willReturn(updatedBranch);
      given(mapper.toBranchResponse(updatedBranch)).willReturn(expectedResponse);

      // Act
      BranchResponse result = updateBranchNameUseCase.execute(branchId, request);

      // Assert
      assertThat(result.name()).isEqualTo(trimmedName);
      verify(branchRepository).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debe actualizar nombre manteniendo otros datos intactos")
    void shouldUpdateNameWhileKeepingOtherDataIntact() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      String newName = "Nuevo Nombre";

      UpdateBranchNameRequest request = new UpdateBranchNameRequest(newName);

      Branch existingBranch =
          Branch.builder()
              .id(branchId)
              .name("Nombre Original")
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      Branch updatedBranch =
          Branch.builder()
              .id(branchId)
              .name(newName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(branchId, newName, franchiseId, new ArrayList<>());

      given(branchRepository.findById(branchId)).willReturn(Optional.of(existingBranch));
      given(branchRepository.save(any(Branch.class))).willReturn(updatedBranch);
      given(mapper.toBranchResponse(updatedBranch)).willReturn(expectedResponse);

      // Act
      BranchResponse result = updateBranchNameUseCase.execute(branchId, request);

      // Assert
      assertThat(result.franchiseId()).isEqualTo(franchiseId);
      assertThat(result.products()).isEmpty();
    }
  }

  @Nested
  @DisplayName("Casos de error")
  class ErrorCases {

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando la sucursal no existe")
    void shouldThrowEntityNotFoundExceptionWhenBranchNotFound() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      UpdateBranchNameRequest request = new UpdateBranchNameRequest("Nuevo Nombre");

      given(branchRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> updateBranchNameUseCase.execute(nonExistentId, request))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Branch")
          .hasMessageContaining(nonExistentId.toString());

      verify(branchRepository).findById(nonExistentId);
      verify(branchRepository, never()).save(any(Branch.class));
      verify(mapper, never()).toBranchResponse(any(Branch.class));
    }

    @Test
    @DisplayName("Debe propagar IllegalArgumentException cuando el nombre es inválido")
    void shouldPropagateIllegalArgumentExceptionWhenNameIsInvalid() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      String invalidName = ""; // Nombre vacío

      UpdateBranchNameRequest request = new UpdateBranchNameRequest(invalidName);

      Branch existingBranch =
          Branch.builder()
              .id(branchId)
              .name("Original Name")
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      given(branchRepository.findById(branchId)).willReturn(Optional.of(existingBranch));

      // Act & Assert
      assertThatThrownBy(() -> updateBranchNameUseCase.execute(branchId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Branch name cannot be blank");

      verify(branchRepository).findById(branchId);
      verify(branchRepository, never()).save(any(Branch.class));
    }
  }

  @Nested
  @DisplayName("Casos de validación")
  class ValidationCases {

    @Test
    @DisplayName("Debe validar que el ID de sucursal no sea null")
    void shouldValidateBranchIdNotNull() {
      // Arrange
      UpdateBranchNameRequest request = new UpdateBranchNameRequest("Nuevo Nombre");

      // Act & Assert
      assertThatThrownBy(() -> updateBranchNameUseCase.execute(null, request))
          .isInstanceOf(Exception.class);
    }
  }
}
