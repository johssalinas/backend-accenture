package com.accenture.franchise.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.UpdateProductNameRequest;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.repository.ProductRepository;
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
 * Pruebas unitarias para {@link UpdateProductNameUseCase}.
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
@DisplayName("UpdateProductNameUseCase - Pruebas Unitarias")
class UpdateProductNameUseCaseTest {

  @Mock private ProductRepository productRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private UpdateProductNameUseCase updateProductNameUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe actualizar nombre de producto correctamente")
    void shouldUpdateProductNameSuccessfully() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String oldName = "Hamburguesa Simple";
      String newName = "Hamburguesa Doble";
      Integer stock = 50;

      UpdateProductNameRequest request = new UpdateProductNameRequest(newName);

      Product existingProduct =
          Product.builder().id(productId).name(oldName).stock(stock).branchId(branchId).build();

      Product updatedProduct =
          Product.builder().id(productId).name(newName).stock(stock).branchId(branchId).build();

      ProductResponse expectedResponse = new ProductResponse(productId, newName, stock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductNameUseCase.execute(productId, request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(productId);
      assertThat(result.name()).isEqualTo(newName);
      assertThat(result.stock()).isEqualTo(stock);
      assertThat(result.branchId()).isEqualTo(branchId);

      verify(productRepository).findById(productId);
      verify(productRepository).save(any(Product.class));
      verify(mapper).toProductResponse(updatedProduct);
    }

    @Test
    @DisplayName("Debe hacer trim del nombre actualizado")
    void shouldTrimUpdatedName() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String nameWithSpaces = "  Pizza Especial  ";
      String trimmedName = "Pizza Especial";
      Integer stock = 30;

      UpdateProductNameRequest request = new UpdateProductNameRequest(nameWithSpaces);

      Product existingProduct =
          Product.builder().id(productId).name("Old Name").stock(stock).branchId(branchId).build();

      Product updatedProduct =
          Product.builder().id(productId).name(trimmedName).stock(stock).branchId(branchId).build();

      ProductResponse expectedResponse =
          new ProductResponse(productId, trimmedName, stock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductNameUseCase.execute(productId, request);

      // Assert
      assertThat(result.name()).isEqualTo(trimmedName);
      verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe actualizar nombre manteniendo stock y branchId intactos")
    void shouldUpdateNameWhileKeepingStockAndBranchIdIntact() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String newName = "Nuevo Producto";
      Integer originalStock = 100;

      UpdateProductNameRequest request = new UpdateProductNameRequest(newName);

      Product existingProduct =
          Product.builder()
              .id(productId)
              .name("Producto Original")
              .stock(originalStock)
              .branchId(branchId)
              .build();

      Product updatedProduct =
          Product.builder()
              .id(productId)
              .name(newName)
              .stock(originalStock)
              .branchId(branchId)
              .build();

      ProductResponse expectedResponse =
          new ProductResponse(productId, newName, originalStock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductNameUseCase.execute(productId, request);

      // Assert
      assertThat(result.stock()).isEqualTo(originalStock);
      assertThat(result.branchId()).isEqualTo(branchId);
    }

    @Test
    @DisplayName("Debe permitir nombres con caracteres especiales")
    void shouldAllowNamesWithSpecialCharacters() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String specialName = "Café & Té (Especial)";
      Integer stock = 25;

      UpdateProductNameRequest request = new UpdateProductNameRequest(specialName);

      Product existingProduct =
          Product.builder().id(productId).name("Old Name").stock(stock).branchId(branchId).build();

      Product updatedProduct =
          Product.builder().id(productId).name(specialName).stock(stock).branchId(branchId).build();

      ProductResponse expectedResponse =
          new ProductResponse(productId, specialName, stock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductNameUseCase.execute(productId, request);

      // Assert
      assertThat(result.name()).isEqualTo(specialName);
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
      UpdateProductNameRequest request = new UpdateProductNameRequest("Nuevo Nombre");

      given(productRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> updateProductNameUseCase.execute(nonExistentId, request))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Product")
          .hasMessageContaining(nonExistentId.toString());

      verify(productRepository).findById(nonExistentId);
      verify(productRepository, never()).save(any(Product.class));
      verify(mapper, never()).toProductResponse(any(Product.class));
    }

    @Test
    @DisplayName("Debe propagar IllegalArgumentException cuando el nombre es inválido")
    void shouldPropagateIllegalArgumentExceptionWhenNameIsInvalid() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String invalidName = ""; // Nombre vacío

      UpdateProductNameRequest request = new UpdateProductNameRequest(invalidName);

      Product existingProduct =
          Product.builder()
              .id(productId)
              .name("Original Name")
              .stock(50)
              .branchId(branchId)
              .build();

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));

      // Act & Assert
      assertThatThrownBy(() -> updateProductNameUseCase.execute(productId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Product name cannot be blank");

      verify(productRepository).findById(productId);
      verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe propagar IllegalArgumentException cuando el nombre es solo espacios")
    void shouldPropagateIllegalArgumentExceptionWhenNameIsOnlySpaces() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String invalidName = "   "; // Solo espacios

      UpdateProductNameRequest request = new UpdateProductNameRequest(invalidName);

      Product existingProduct =
          Product.builder()
              .id(productId)
              .name("Original Name")
              .stock(50)
              .branchId(branchId)
              .build();

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));

      // Act & Assert
      assertThatThrownBy(() -> updateProductNameUseCase.execute(productId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Product name cannot be blank");

      verify(productRepository).findById(productId);
      verify(productRepository, never()).save(any(Product.class));
    }
  }

  @Nested
  @DisplayName("Casos de validación")
  class ValidationCases {

    @Test
    @DisplayName("Debe validar que el ID de producto no sea null")
    void shouldValidateProductIdNotNull() {
      // Arrange
      UpdateProductNameRequest request = new UpdateProductNameRequest("Nuevo Nombre");

      // Act & Assert
      assertThatThrownBy(() -> updateProductNameUseCase.execute(null, request))
          .isInstanceOf(Exception.class);
    }
  }
}
