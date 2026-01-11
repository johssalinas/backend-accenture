package com.accenture.franchise.application.usecase.branch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.CreateBranchRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.repository.BranchRepository;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas unitarias para {@link AddBranchToFranchiseUseCase}. */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddBranchToFranchiseUseCase - Pruebas Unitarias")
class AddBranchToFranchiseUseCaseTest {

  @Mock private FranchiseRepository franchiseRepository;

  @Mock private BranchRepository branchRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe agregar sucursal correctamente a franquicia existente")
    void shouldAddBranchToFranchiseSuccessfully() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Sucursal Centro";

      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      UUID branchId = UUID.randomUUID();
      Branch savedBranch =
          Branch.builder()
              .id(branchId)
              .name(branchName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(branchId, branchName, franchiseId, new ArrayList<>());

      given(franchiseRepository.existsById(franchiseId)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(branchName, franchiseId)).willReturn(false);
      given(branchRepository.save(any(Branch.class))).willReturn(savedBranch);
      given(mapper.toBranchResponse(savedBranch)).willReturn(expectedResponse);

      // Act
      BranchResponse result = addBranchToFranchiseUseCase.execute(request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(branchId);
      assertThat(result.name()).isEqualTo(branchName);
      assertThat(result.franchiseId()).isEqualTo(franchiseId);

      verify(franchiseRepository).existsById(franchiseId);
      verify(branchRepository).existsByNameAndFranchiseId(branchName, franchiseId);
      verify(branchRepository).save(any(Branch.class));
      verify(mapper).toBranchResponse(savedBranch);
    }

    @Test
    @DisplayName("Debe crear sucursal con nombre único en la franquicia")
    void shouldCreateBranchWithUniqueName() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Sucursal Norte";

      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      UUID branchId = UUID.randomUUID();
      Branch savedBranch =
          Branch.builder()
              .id(branchId)
              .name(branchName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(branchId, branchName, franchiseId, new ArrayList<>());

      given(franchiseRepository.existsById(franchiseId)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(branchName, franchiseId)).willReturn(false);
      given(branchRepository.save(any(Branch.class))).willReturn(savedBranch);
      given(mapper.toBranchResponse(savedBranch)).willReturn(expectedResponse);

      // Act
      BranchResponse result = addBranchToFranchiseUseCase.execute(request);

      // Assert
      assertThat(result.name()).isEqualTo(branchName);
      verify(branchRepository).existsByNameAndFranchiseId(branchName, franchiseId);
    }

    @Test
    @DisplayName("Debe capturar la sucursal guardada correctamente")
    void shouldCaptureTheSavedBranchCorrectly() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Sucursal Sur";

      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      Branch savedBranch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name(branchName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(savedBranch.getId(), branchName, franchiseId, new ArrayList<>());

      given(franchiseRepository.existsById(franchiseId)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(branchName, franchiseId)).willReturn(false);
      given(branchRepository.save(any(Branch.class))).willReturn(savedBranch);
      given(mapper.toBranchResponse(savedBranch)).willReturn(expectedResponse);

      // Act
      addBranchToFranchiseUseCase.execute(request);

      // Assert
      ArgumentCaptor<Branch> branchCaptor = ArgumentCaptor.forClass(Branch.class);
      verify(branchRepository).save(branchCaptor.capture());

      Branch capturedBranch = branchCaptor.getValue();
      assertThat(capturedBranch.getName()).isEqualTo(branchName);
      assertThat(capturedBranch.getFranchiseId()).isEqualTo(franchiseId);
    }
  }

  @Nested
  @DisplayName("Casos de error")
  class ErrorCases {

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando la franquicia no existe")
    void shouldThrowEntityNotFoundExceptionWhenFranchiseDoesNotExist() {
      // Arrange
      UUID nonExistentFranchiseId = UUID.randomUUID();
      String branchName = "Sucursal Test";

      CreateBranchRequest request = new CreateBranchRequest(nonExistentFranchiseId, branchName);

      given(franchiseRepository.existsById(nonExistentFranchiseId)).willReturn(false);

      // Act & Assert
      assertThatThrownBy(() -> addBranchToFranchiseUseCase.execute(request))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Franchise")
          .hasMessageContaining(nonExistentFranchiseId.toString());

      verify(franchiseRepository).existsById(nonExistentFranchiseId);
      verify(branchRepository, never()).existsByNameAndFranchiseId(any(), any());
      verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Debe lanzar BusinessRuleViolationException cuando el nombre ya existe")
    void shouldThrowBusinessRuleViolationExceptionWhenNameAlreadyExists() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String duplicateName = "Sucursal Duplicada";

      CreateBranchRequest request = new CreateBranchRequest(franchiseId, duplicateName);

      given(franchiseRepository.existsById(franchiseId)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(duplicateName, franchiseId))
          .willReturn(true);

      // Act & Assert
      assertThatThrownBy(() -> addBranchToFranchiseUseCase.execute(request))
          .isInstanceOf(BusinessRuleViolationException.class)
          .hasMessageContaining("Branch with name already exists")
          .hasMessageContaining(duplicateName);

      verify(franchiseRepository).existsById(franchiseId);
      verify(branchRepository).existsByNameAndFranchiseId(duplicateName, franchiseId);
      verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("No debe guardar si la validación falla")
    void shouldNotSaveIfValidationFails() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Test Branch";

      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      given(franchiseRepository.existsById(franchiseId)).willReturn(false);

      // Act & Assert
      assertThatThrownBy(() -> addBranchToFranchiseUseCase.execute(request))
          .isInstanceOf(EntityNotFoundException.class);

      verify(branchRepository, never()).save(any(Branch.class));
      verify(mapper, never()).toBranchResponse(any(Branch.class));
    }
  }

  @Nested
  @DisplayName("Verificación de interacciones")
  class InteractionTests {

    @Test
    @DisplayName("Debe validar franquicia antes de verificar nombre duplicado")
    void shouldValidateFranchiseBeforeCheckingDuplicateName() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Test Branch";

      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      Branch savedBranch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name(branchName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(savedBranch.getId(), branchName, franchiseId, new ArrayList<>());

      given(franchiseRepository.existsById(franchiseId)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(branchName, franchiseId)).willReturn(false);
      given(branchRepository.save(any(Branch.class))).willReturn(savedBranch);
      given(mapper.toBranchResponse(savedBranch)).willReturn(expectedResponse);

      // Act
      addBranchToFranchiseUseCase.execute(request);

      // Assert
      var inOrder = org.mockito.Mockito.inOrder(franchiseRepository, branchRepository, mapper);
      inOrder.verify(franchiseRepository).existsById(franchiseId);
      inOrder.verify(branchRepository).existsByNameAndFranchiseId(branchName, franchiseId);
      inOrder.verify(branchRepository).save(any(Branch.class));
      inOrder.verify(mapper).toBranchResponse(savedBranch);
    }

    @Test
    @DisplayName("Debe guardar sucursal exactamente una vez")
    void shouldSaveBranchExactlyOnce() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Test Branch";

      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      Branch savedBranch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name(branchName)
              .franchiseId(franchiseId)
              .products(new ArrayList<>())
              .build();

      BranchResponse expectedResponse =
          new BranchResponse(savedBranch.getId(), branchName, franchiseId, new ArrayList<>());

      given(franchiseRepository.existsById(franchiseId)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(branchName, franchiseId)).willReturn(false);
      given(branchRepository.save(any(Branch.class))).willReturn(savedBranch);
      given(mapper.toBranchResponse(savedBranch)).willReturn(expectedResponse);

      // Act
      addBranchToFranchiseUseCase.execute(request);

      // Assert
      verify(branchRepository, org.mockito.Mockito.times(1)).save(any(Branch.class));
    }
  }

  @Nested
  @DisplayName("Validaciones de reglas de negocio")
  class BusinessRuleValidations {

    @Test
    @DisplayName("Debe permitir mismo nombre en diferentes franquicias")
    void shouldAllowSameNameInDifferentFranchises() {
      // Arrange
      UUID franchiseId1 = UUID.randomUUID();
      UUID franchiseId2 = UUID.randomUUID();
      String sameBranchName = "Sucursal Centro";

      CreateBranchRequest request1 = new CreateBranchRequest(franchiseId1, sameBranchName);
      CreateBranchRequest request2 = new CreateBranchRequest(franchiseId2, sameBranchName);

      Branch savedBranch1 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name(sameBranchName)
              .franchiseId(franchiseId1)
              .products(new ArrayList<>())
              .build();

      Branch savedBranch2 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name(sameBranchName)
              .franchiseId(franchiseId2)
              .products(new ArrayList<>())
              .build();

      BranchResponse response1 =
          new BranchResponse(savedBranch1.getId(), sameBranchName, franchiseId1, new ArrayList<>());
      BranchResponse response2 =
          new BranchResponse(savedBranch2.getId(), sameBranchName, franchiseId2, new ArrayList<>());

      // Primera franquicia
      given(franchiseRepository.existsById(franchiseId1)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(sameBranchName, franchiseId1))
          .willReturn(false);
      given(branchRepository.save(any(Branch.class))).willReturn(savedBranch1);
      given(mapper.toBranchResponse(savedBranch1)).willReturn(response1);

      // Act & Assert - Primera sucursal
      BranchResponse result1 = addBranchToFranchiseUseCase.execute(request1);
      assertThat(result1.name()).isEqualTo(sameBranchName);
      assertThat(result1.franchiseId()).isEqualTo(franchiseId1);

      // Segunda franquicia
      given(franchiseRepository.existsById(franchiseId2)).willReturn(true);
      given(branchRepository.existsByNameAndFranchiseId(sameBranchName, franchiseId2))
          .willReturn(false);
      given(branchRepository.save(any(Branch.class))).willReturn(savedBranch2);
      given(mapper.toBranchResponse(savedBranch2)).willReturn(response2);

      // Act & Assert - Segunda sucursal
      BranchResponse result2 = addBranchToFranchiseUseCase.execute(request2);
      assertThat(result2.name()).isEqualTo(sameBranchName);
      assertThat(result2.franchiseId()).isEqualTo(franchiseId2);

      // Verificar que se llamó dos veces al método save
      verify(branchRepository, org.mockito.Mockito.times(2)).save(any(Branch.class));
    }
  }
}
