package com.accenture.franchise.application.usecase.franchise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.CreateFranchiseRequest;
import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link CreateFranchiseUseCase}.
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
@DisplayName("CreateFranchiseUseCase - Pruebas Unitarias")
class CreateFranchiseUseCaseTest {

  @Mock private FranchiseRepository franchiseRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private CreateFranchiseUseCase createFranchiseUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe crear franquicia correctamente con datos válidos")
    void shouldCreateFranchiseSuccessfully() {
      // Arrange
      String franchiseName = "McDonald's";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      UUID expectedId = UUID.randomUUID();
      Franchise savedFranchise =
          Franchise.builder()
              .id(expectedId)
              .name(franchiseName)
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(expectedId, franchiseName, new ArrayList<>());

      given(franchiseRepository.existsByName(franchiseName)).willReturn(false);
      given(franchiseRepository.save(any(Franchise.class))).willReturn(savedFranchise);
      given(mapper.toFranchiseResponse(savedFranchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = createFranchiseUseCase.execute(request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(expectedId);
      assertThat(result.name()).isEqualTo(franchiseName);
      assertThat(result.branches()).isEmpty();

      verify(franchiseRepository).existsByName(franchiseName);
      verify(franchiseRepository).save(any(Franchise.class));
      verify(mapper).toFranchiseResponse(savedFranchise);
    }

    @Test
    @DisplayName("Debe crear franquicia con nombre que contiene espacios")
    void shouldCreateFranchiseWithNameContainingSpaces() {
      // Arrange
      String franchiseName = "Burger King";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      UUID expectedId = UUID.randomUUID();
      Franchise savedFranchise =
          Franchise.builder()
              .id(expectedId)
              .name(franchiseName)
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(expectedId, franchiseName, new ArrayList<>());

      given(franchiseRepository.existsByName(franchiseName)).willReturn(false);
      given(franchiseRepository.save(any(Franchise.class))).willReturn(savedFranchise);
      given(mapper.toFranchiseResponse(savedFranchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = createFranchiseUseCase.execute(request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.name()).isEqualTo(franchiseName);
      verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Debe crear franquicia con nombre único largo")
    void shouldCreateFranchiseWithLongUniqueName() {
      // Arrange
      String franchiseName = "Super Mega Ultimate Restaurant Chain International Corporation LLC";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      UUID expectedId = UUID.randomUUID();
      Franchise savedFranchise =
          Franchise.builder()
              .id(expectedId)
              .name(franchiseName)
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(expectedId, franchiseName, new ArrayList<>());

      given(franchiseRepository.existsByName(franchiseName)).willReturn(false);
      given(franchiseRepository.save(any(Franchise.class))).willReturn(savedFranchise);
      given(mapper.toFranchiseResponse(savedFranchise)).willReturn(expectedResponse);

      // Act
      FranchiseResponse result = createFranchiseUseCase.execute(request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.name()).hasSize(franchiseName.length());
      verify(franchiseRepository).save(any(Franchise.class));
    }
  }

  @Nested
  @DisplayName("Casos de error")
  class ErrorCases {

    @Test
    @DisplayName("Debe lanzar excepción cuando el nombre de franquicia ya existe")
    void shouldThrowExceptionWhenFranchiseNameAlreadyExists() {
      // Arrange
      String franchiseName = "McDonald's";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      given(franchiseRepository.existsByName(franchiseName)).willReturn(true);

      // Act & Assert
      assertThatThrownBy(() -> createFranchiseUseCase.execute(request))
          .isInstanceOf(BusinessRuleViolationException.class)
          .hasMessageContaining("Franchise with name already exists")
          .hasMessageContaining(franchiseName);

      verify(franchiseRepository).existsByName(franchiseName);
      verify(franchiseRepository, never()).save(any(Franchise.class));
      verify(mapper, never()).toFranchiseResponse(any(Franchise.class));
    }

    @Test
    @DisplayName("Debe verificar existencia antes de guardar")
    void shouldCheckExistenceBeforeSaving() {
      // Arrange
      String franchiseName = "Test Franchise";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      given(franchiseRepository.existsByName(franchiseName)).willReturn(true);

      // Act & Assert
      assertThatThrownBy(() -> createFranchiseUseCase.execute(request))
          .isInstanceOf(BusinessRuleViolationException.class);

      verify(franchiseRepository).existsByName(franchiseName);
      verify(franchiseRepository, never()).save(any(Franchise.class));
    }
  }

  @Nested
  @DisplayName("Verificación de interacciones")
  class InteractionTests {

    @Test
    @DisplayName("Debe llamar al repositorio en el orden correcto")
    void shouldCallRepositoryInCorrectOrder() {
      // Arrange
      String franchiseName = "Test";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      UUID expectedId = UUID.randomUUID();
      Franchise savedFranchise =
          Franchise.builder()
              .id(expectedId)
              .name(franchiseName)
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(expectedId, franchiseName, new ArrayList<>());

      given(franchiseRepository.existsByName(anyString())).willReturn(false);
      given(franchiseRepository.save(any(Franchise.class))).willReturn(savedFranchise);
      given(mapper.toFranchiseResponse(any(Franchise.class))).willReturn(expectedResponse);

      // Act
      createFranchiseUseCase.execute(request);

      // Assert - Verificar orden de llamadas
      var inOrder = org.mockito.Mockito.inOrder(franchiseRepository, mapper);
      inOrder.verify(franchiseRepository).existsByName(franchiseName);
      inOrder.verify(franchiseRepository).save(any(Franchise.class));
      inOrder.verify(mapper).toFranchiseResponse(savedFranchise);
    }

    @Test
    @DisplayName("Debe llamar al mapper exactamente una vez")
    void shouldCallMapperExactlyOnce() {
      // Arrange
      String franchiseName = "Test";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      Franchise savedFranchise =
          Franchise.builder()
              .id(UUID.randomUUID())
              .name(franchiseName)
              .branches(new ArrayList<>())
              .build();

      FranchiseResponse expectedResponse =
          new FranchiseResponse(savedFranchise.getId(), franchiseName, new ArrayList<>());

      given(franchiseRepository.existsByName(franchiseName)).willReturn(false);
      given(franchiseRepository.save(any(Franchise.class))).willReturn(savedFranchise);
      given(mapper.toFranchiseResponse(savedFranchise)).willReturn(expectedResponse);

      // Act
      createFranchiseUseCase.execute(request);

      // Assert
      verify(mapper, org.mockito.Mockito.times(1)).toFranchiseResponse(savedFranchise);
    }
  }
}
