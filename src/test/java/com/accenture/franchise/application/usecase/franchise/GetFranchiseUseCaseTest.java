package com.accenture.franchise.application.usecase.franchise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.FranchiseResponse;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas unitarias para {@link GetFranchiseUseCase}. */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetFranchiseUseCase - Pruebas Unitarias")
class GetFranchiseUseCaseTest {

  @Mock private FranchiseRepository franchiseRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private GetFranchiseUseCase getFranchiseUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe obtener franquicia correctamente por ID existente")
    void shouldGetFranchiseSuccessfully() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String franchiseName = "McDonald's";

      Franchise franchise =
          Franchise.builder().id(franchiseId).name(franchiseName).branches(new ArrayList<>()).build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, franchiseName, new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = getFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(franchiseId);
      assertThat(result.name()).isEqualTo(franchiseName);
      assertThat(result.branches()).isEmpty();

      verify(franchiseRepository).findById(franchiseId);
      verify(mapper).toFranchiseResponse(franchise);
    }

    @Test
    @DisplayName("Debe obtener franquicia con todas sus propiedades")
    void shouldGetFranchiseWithAllProperties() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String franchiseName = "Starbucks";

      Franchise franchise =
          Franchise.builder().id(franchiseId).name(franchiseName).branches(new ArrayList<>()).build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, franchiseName, new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = getFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result)
          .isNotNull()
          .satisfies(
              response -> {
                assertThat(response.id()).isEqualTo(franchiseId);
                assertThat(response.name()).isEqualTo(franchiseName);
                assertThat(response.branches()).isNotNull().isEmpty();
              });
    }

    @Test
    @DisplayName("Debe llamar al mapper con el objeto correcto")
    void shouldCallMapperWithCorrectObject() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      Franchise franchise =
          Franchise.builder()
              .id(franchiseId)
              .name("Test Franchise")
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, "Test Franchise", new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      getFranchiseUseCase.execute(franchiseId);

      // Assert
      verify(mapper).toFranchiseResponse(franchise);
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
      given(franchiseRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> getFranchiseUseCase.execute(nonExistentId))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Franchise")
          .hasMessageContaining(nonExistentId.toString());

      verify(franchiseRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Debe lanzar excepción con mensaje descriptivo")
    void shouldThrowExceptionWithDescriptiveMessage() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      given(franchiseRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> getFranchiseUseCase.execute(nonExistentId))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessage("Franchise not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("No debe llamar al mapper cuando la franquicia no existe")
    void shouldNotCallMapperWhenFranchiseDoesNotExist() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      given(franchiseRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> getFranchiseUseCase.execute(nonExistentId))
          .isInstanceOf(EntityNotFoundException.class);

      verify(franchiseRepository).findById(nonExistentId);
      verify(mapper, org.mockito.Mockito.never()).toFranchiseResponse(org.mockito.ArgumentMatchers.any());
    }
  }

  @Nested
  @DisplayName("Casos extremos")
  class EdgeCases {

    @Test
    @DisplayName("Debe manejar UUID válidos de diferentes formatos")
    void shouldHandleValidUUIDsOfDifferentFormats() {
      // Arrange
      UUID franchiseId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
      Franchise franchise =
          Franchise.builder()
              .id(franchiseId)
              .name("Test Franchise")
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, "Test Franchise", new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = getFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(franchiseId);
    }

    @Test
    @DisplayName("Debe buscar franquicia solo una vez en el repositorio")
    void shouldQueryRepositoryOnlyOnce() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      Franchise franchise =
          Franchise.builder()
              .id(franchiseId)
              .name("Test Franchise")
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, "Test Franchise", new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      getFranchiseUseCase.execute(franchiseId);

      // Assert
      verify(franchiseRepository, org.mockito.Mockito.times(1)).findById(franchiseId);
    }
  }

  @Nested
  @DisplayName("Verificación de transaccionalidad")
  class TransactionalBehavior {

    @Test
    @DisplayName("Debe ser operación de solo lectura")
    void shouldBeReadOnlyOperation() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      Franchise franchise =
          Franchise.builder()
              .id(franchiseId)
              .name("Test Franchise")
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(franchiseId, "Test Franchise", new ArrayList<>());

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toFranchiseResponse(franchise)).willReturn(expectedResponse);

      // Act
      getFranchiseUseCase.execute(franchiseId);

      // Assert - Solo debe haber operaciones de lectura
      verify(franchiseRepository).findById(franchiseId);
      verify(franchiseRepository, org.mockito.Mockito.never())
          .save(org.mockito.ArgumentMatchers.any());
    }
  }
}
