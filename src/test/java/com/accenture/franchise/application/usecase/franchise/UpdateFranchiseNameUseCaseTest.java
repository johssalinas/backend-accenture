package com.accenture.franchise.application.usecase.franchise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.UpdateFranchiseNameRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas unitarias para {@link UpdateFranchiseNameUseCase}. */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateFranchiseNameUseCase - Pruebas Unitarias")
class UpdateFranchiseNameUseCaseTest {

  @Mock private FranchiseRepository franchiseRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe actualizar el nombre de franquicia correctamente")
    void shouldUpdateFranchiseNameSuccessfully() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String oldName = "Old Name";
      String newName = "New Name";

      Franchise franchise =
          Franchise.builder().id(franchiseId).name(oldName).branches(new ArrayList<>()).build();

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest(newName);

      // Simular que el nombre se actualiza
      franchise.updateName(newName);

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, newName, new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(franchiseRepository.save(franchise)).willReturn(franchise);
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = updateFranchiseNameUseCase.execute(franchiseId, request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.name()).isEqualTo(newName);

      verify(franchiseRepository).findById(franchiseId);
      verify(franchiseRepository).save(franchise);
      verify(mapper).toFranchiseResponse(franchise);
    }

    @Test
    @DisplayName("Debe actualizar nombre con espacios al inicio y final (trimming)")
    void shouldUpdateNameWithTrimmingSpaces() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String newName = "  New Name  ";

      Franchise franchise =
          Franchise.builder().id(franchiseId).name("Old Name").branches(new ArrayList<>()).build();

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest(newName);

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, newName.trim(), new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(franchiseRepository.save(franchise)).willReturn(franchise);
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = updateFranchiseNameUseCase.execute(franchiseId, request);

      // Assert
      assertThat(result.name()).isEqualTo(newName.trim());
    }

    @Test
    @DisplayName("Debe capturar el objeto guardado correctamente")
    void shouldCaptureTheSavedObjectCorrectly() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String newName = "Updated Name";

      Franchise franchise =
          Franchise.builder().id(franchiseId).name("Old Name").branches(new ArrayList<>()).build();

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest(newName);

      franchise.updateName(newName);

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, newName, new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(franchiseRepository.save(any(Franchise.class))).willReturn(franchise);
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      updateFranchiseNameUseCase.execute(franchiseId, request);

      // Assert
      ArgumentCaptor<Franchise> franchiseCaptor = ArgumentCaptor.forClass(Franchise.class);
      verify(franchiseRepository).save(franchiseCaptor.capture());

      Franchise capturedFranchise = franchiseCaptor.getValue();
      assertThat(capturedFranchise.getName()).isEqualTo(newName);
      assertThat(capturedFranchise.getId()).isEqualTo(franchiseId);
    }
  }

  @Nested
  @DisplayName("Casos de error")
  class ErrorCases {

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando la franquicia no existe")
    void shouldThrowEntityNotFoundExceptionWhenFranchiseDoesNotExist() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest("New Name");

      given(franchiseRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> updateFranchiseNameUseCase.execute(nonExistentId, request))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Franchise")
          .hasMessageContaining(nonExistentId.toString());

      verify(franchiseRepository).findById(nonExistentId);
      verify(franchiseRepository, never()).save(any(Franchise.class));
      verify(mapper, never()).toFranchiseResponse(any(Franchise.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Debe lanzar excepción cuando el nombre es nulo, vacío o solo espacios")
    void shouldThrowExceptionWhenNameIsNullOrBlank(String invalidName) {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      Franchise franchise =
          Franchise.builder()
              .id(franchiseId)
              .name("Original Name")
              .branches(new ArrayList<>())
              .build();

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest(invalidName);

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));

      // Act & Assert
      assertThatThrownBy(() -> updateFranchiseNameUseCase.execute(franchiseId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("name cannot be blank");

      verify(franchiseRepository).findById(franchiseId);
      verify(franchiseRepository, never()).save(any(Franchise.class));
    }

    @Test
    @DisplayName("No debe guardar si la actualización falla")
    void shouldNotSaveIfUpdateFails() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      Franchise franchise =
          Franchise.builder()
              .id(franchiseId)
              .name("Original Name")
              .branches(new ArrayList<>())
              .build();

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest("");

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));

      // Act & Assert
      assertThatThrownBy(() -> updateFranchiseNameUseCase.execute(franchiseId, request))
          .isInstanceOf(IllegalArgumentException.class);

      verify(franchiseRepository, never()).save(any(Franchise.class));
    }
  }

  @Nested
  @DisplayName("Verificación de interacciones")
  class InteractionTests {

    @Test
    @DisplayName("Debe llamar métodos en el orden correcto")
    void shouldCallMethodsInCorrectOrder() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String newName = "Updated Name";

      Franchise franchise =
          Franchise.builder().id(franchiseId).name("Old Name").branches(new ArrayList<>()).build();

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest(newName);

      franchise.updateName(newName);

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, newName, new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(franchiseRepository.save(franchise)).willReturn(franchise);
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      updateFranchiseNameUseCase.execute(franchiseId, request);

      // Assert
      var inOrder = org.mockito.Mockito.inOrder(franchiseRepository, mapper);
      inOrder.verify(franchiseRepository).findById(franchiseId);
      inOrder.verify(franchiseRepository).save(franchise);
      inOrder.verify(mapper).toFranchiseResponse(franchise);
    }

    @Test
    @DisplayName("Debe guardar exactamente una vez")
    void shouldSaveExactlyOnce() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String newName = "Updated Name";

      Franchise franchise =
          Franchise.builder().id(franchiseId).name("Old Name").branches(new ArrayList<>()).build();

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest(newName);

      franchise.updateName(newName);

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, newName, new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(franchiseRepository.save(franchise)).willReturn(franchise);
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      updateFranchiseNameUseCase.execute(franchiseId, request);

      // Assert
      verify(franchiseRepository, org.mockito.Mockito.times(1)).save(any(Franchise.class));
    }
  }
}
