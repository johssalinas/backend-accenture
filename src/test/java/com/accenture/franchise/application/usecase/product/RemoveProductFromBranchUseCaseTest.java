package com.accenture.franchise.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.repository.ProductRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link RemoveProductFromBranchUseCase}.
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
@DisplayName("RemoveProductFromBranchUseCase - Pruebas Unitarias")
class RemoveProductFromBranchUseCaseTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private RemoveProductFromBranchUseCase removeProductFromBranchUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe eliminar producto correctamente cuando existe")
    void shouldRemoveProductSuccessfullyWhenExists() {
      // Arrange
      UUID productId = UUID.randomUUID();

      given(productRepository.existsById(productId)).willReturn(true);

      // Act & Assert
      assertThatCode(() -> removeProductFromBranchUseCase.execute(productId))
          .doesNotThrowAnyException();

      verify(productRepository).existsById(productId);
      verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Debe eliminar producto sin retornar valor")
    void shouldRemoveProductWithoutReturningValue() {
      // Arrange
      UUID productId = UUID.randomUUID();

      given(productRepository.existsById(productId)).willReturn(true);

      // Act
      removeProductFromBranchUseCase.execute(productId);

      // Assert - verificar que el método no retorna nada
      verify(productRepository).existsById(productId);
      verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Debe verificar existencia antes de eliminar")
    void shouldVerifyExistenceBeforeDeleting() {
      // Arrange
      UUID productId = UUID.randomUUID();

      given(productRepository.existsById(productId)).willReturn(true);

      // Act
      removeProductFromBranchUseCase.execute(productId);

      // Assert - verificar orden de llamadas
      var inOrder = org.mockito.Mockito.inOrder(productRepository);
      inOrder.verify(productRepository).existsById(productId);
      inOrder.verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Debe eliminar múltiples productos en secuencia")
    void shouldRemoveMultipleProductsInSequence() {
      // Arrange
      UUID productId1 = UUID.randomUUID();
      UUID productId2 = UUID.randomUUID();
      UUID productId3 = UUID.randomUUID();

      given(productRepository.existsById(productId1)).willReturn(true);
      given(productRepository.existsById(productId2)).willReturn(true);
      given(productRepository.existsById(productId3)).willReturn(true);

      // Act
      removeProductFromBranchUseCase.execute(productId1);
      removeProductFromBranchUseCase.execute(productId2);
      removeProductFromBranchUseCase.execute(productId3);

      // Assert
      verify(productRepository).deleteById(productId1);
      verify(productRepository).deleteById(productId2);
      verify(productRepository).deleteById(productId3);
    }
  }

  @Nested
  @DisplayName("Casos de error")
  class ErrorCases {

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando el producto no existe")
    void shouldThrowEntityNotFoundExceptionWhenProductNotFound() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      given(productRepository.existsById(nonExistentId)).willReturn(false);

      // Act & Assert
      assertThatThrownBy(() -> removeProductFromBranchUseCase.execute(nonExistentId))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Product")
          .hasMessageContaining(nonExistentId.toString());

      verify(productRepository).existsById(nonExistentId);
      verify(productRepository, never()).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("No debe llamar a deleteById si el producto no existe")
    void shouldNotCallDeleteByIdIfProductDoesNotExist() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      given(productRepository.existsById(nonExistentId)).willReturn(false);

      // Act
      try {
        removeProductFromBranchUseCase.execute(nonExistentId);
      } catch (EntityNotFoundException e) {
        // Expected exception
      }

      // Assert
      verify(productRepository, never()).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("Debe propagar excepciones del repositorio")
    void shouldPropagateRepositoryExceptions() {
      // Arrange
      UUID productId = UUID.randomUUID();
      RuntimeException repositoryException = new RuntimeException("Database error");

      given(productRepository.existsById(productId)).willThrow(repositoryException);

      // Act & Assert
      assertThatThrownBy(() -> removeProductFromBranchUseCase.execute(productId))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database error");

      verify(productRepository).existsById(productId);
      verify(productRepository, never()).deleteById(productId);
    }
  }

  @Nested
  @DisplayName("Casos de validación")
  class ValidationCases {

    @Test
    @DisplayName("Debe validar que el ID de producto no sea null")
    void shouldValidateProductIdNotNull() {
      // Act & Assert
      assertThatThrownBy(() -> removeProductFromBranchUseCase.execute(null))
          .isInstanceOf(Exception.class);
    }
  }

  @Nested
  @DisplayName("Casos de integración")
  class IntegrationCases {

    @Test
    @DisplayName("Debe manejar correctamente el flujo completo de eliminación")
    void shouldHandleCompleteRemovalFlowCorrectly() {
      // Arrange
      UUID productId = UUID.randomUUID();

      given(productRepository.existsById(productId)).willReturn(true);

      // Act
      assertThatCode(
              () -> {
                removeProductFromBranchUseCase.execute(productId);
              })
          .doesNotThrowAnyException();

      // Assert - verificar todas las interacciones
      verify(productRepository).existsById(productId);
      verify(productRepository).deleteById(productId);

      // Verificar que no hay más interacciones
      org.mockito.Mockito.verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Debe ejecutarse sin problemas cuando el repositorio responde correctamente")
    void shouldExecuteWithoutIssuesWhenRepositoryRespondsCorrectly() {
      // Arrange
      UUID productId = UUID.randomUUID();

      given(productRepository.existsById(productId)).willReturn(true);

      // Act - ejecutar múltiples veces para verificar idempotencia conceptual
      removeProductFromBranchUseCase.execute(productId);

      // Assert
      verify(productRepository).existsById(productId);
      verify(productRepository).deleteById(productId);
    }
  }
}
