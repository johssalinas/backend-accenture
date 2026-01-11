package com.accenture.franchise.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.UpdateProductStockRequest;
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
 * Pruebas unitarias para {@link UpdateProductStockUseCase}.
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
@DisplayName("UpdateProductStockUseCase - Pruebas Unitarias")
class UpdateProductStockUseCaseTest {

  @Mock private ProductRepository productRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private UpdateProductStockUseCase updateProductStockUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe actualizar stock de producto correctamente")
    void shouldUpdateProductStockSuccessfully() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String name = "Hamburguesa";
      Integer oldStock = 50;
      Integer newStock = 100;

      UpdateProductStockRequest request = new UpdateProductStockRequest(newStock);

      Product existingProduct =
          Product.builder().id(productId).name(name).stock(oldStock).branchId(branchId).build();

      Product updatedProduct =
          Product.builder().id(productId).name(name).stock(newStock).branchId(branchId).build();

      ProductResponse expectedResponse = new ProductResponse(productId, name, newStock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductStockUseCase.execute(productId, request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(productId);
      assertThat(result.name()).isEqualTo(name);
      assertThat(result.stock()).isEqualTo(newStock);
      assertThat(result.branchId()).isEqualTo(branchId);

      verify(productRepository).findById(productId);
      verify(productRepository).save(any(Product.class));
      verify(mapper).toProductResponse(updatedProduct);
    }

    @Test
    @DisplayName("Debe permitir actualizar stock a cero")
    void shouldAllowUpdatingStockToZero() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String name = "Pizza";
      Integer oldStock = 50;
      Integer newStock = 0;

      UpdateProductStockRequest request = new UpdateProductStockRequest(newStock);

      Product existingProduct =
          Product.builder().id(productId).name(name).stock(oldStock).branchId(branchId).build();

      Product updatedProduct =
          Product.builder().id(productId).name(name).stock(newStock).branchId(branchId).build();

      ProductResponse expectedResponse = new ProductResponse(productId, name, newStock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductStockUseCase.execute(productId, request);

      // Assert
      assertThat(result.stock()).isEqualTo(0);
      verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe actualizar stock manteniendo nombre y branchId intactos")
    void shouldUpdateStockWhileKeepingNameAndBranchIdIntact() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String originalName = "Tacos";
      Integer oldStock = 25;
      Integer newStock = 75;

      UpdateProductStockRequest request = new UpdateProductStockRequest(newStock);

      Product existingProduct =
          Product.builder()
              .id(productId)
              .name(originalName)
              .stock(oldStock)
              .branchId(branchId)
              .build();

      Product updatedProduct =
          Product.builder()
              .id(productId)
              .name(originalName)
              .stock(newStock)
              .branchId(branchId)
              .build();

      ProductResponse expectedResponse =
          new ProductResponse(productId, originalName, newStock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductStockUseCase.execute(productId, request);

      // Assert
      assertThat(result.name()).isEqualTo(originalName);
      assertThat(result.branchId()).isEqualTo(branchId);
    }

    @Test
    @DisplayName("Debe permitir actualizar stock a valores grandes")
    void shouldAllowUpdatingStockToLargeValues() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String name = "Producto";
      Integer oldStock = 10;
      Integer newStock = 10000;

      UpdateProductStockRequest request = new UpdateProductStockRequest(newStock);

      Product existingProduct =
          Product.builder().id(productId).name(name).stock(oldStock).branchId(branchId).build();

      Product updatedProduct =
          Product.builder().id(productId).name(name).stock(newStock).branchId(branchId).build();

      ProductResponse expectedResponse = new ProductResponse(productId, name, newStock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductStockUseCase.execute(productId, request);

      // Assert
      assertThat(result.stock()).isEqualTo(10000);
    }

    @Test
    @DisplayName("Debe permitir reducir el stock")
    void shouldAllowReducingStock() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String name = "Producto";
      Integer oldStock = 100;
      Integer newStock = 30;

      UpdateProductStockRequest request = new UpdateProductStockRequest(newStock);

      Product existingProduct =
          Product.builder().id(productId).name(name).stock(oldStock).branchId(branchId).build();

      Product updatedProduct =
          Product.builder().id(productId).name(name).stock(newStock).branchId(branchId).build();

      ProductResponse expectedResponse = new ProductResponse(productId, name, newStock, branchId);

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
      given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
      given(mapper.toProductResponse(updatedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = updateProductStockUseCase.execute(productId, request);

      // Assert
      assertThat(result.stock()).isLessThan(oldStock);
      assertThat(result.stock()).isEqualTo(30);
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
      UpdateProductStockRequest request = new UpdateProductStockRequest(100);

      given(productRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> updateProductStockUseCase.execute(nonExistentId, request))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Product")
          .hasMessageContaining(nonExistentId.toString());

      verify(productRepository).findById(nonExistentId);
      verify(productRepository, never()).save(any(Product.class));
      verify(mapper, never()).toProductResponse(any(Product.class));
    }

    @Test
    @DisplayName("Debe propagar IllegalArgumentException cuando el stock es negativo")
    void shouldPropagateIllegalArgumentExceptionWhenStockIsNegative() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      Integer invalidStock = -10;

      UpdateProductStockRequest request = new UpdateProductStockRequest(invalidStock);

      Product existingProduct =
          Product.builder().id(productId).name("Product").stock(50).branchId(branchId).build();

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));

      // Act & Assert
      assertThatThrownBy(() -> updateProductStockUseCase.execute(productId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Stock must be a positive number");

      verify(productRepository).findById(productId);
      verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe propagar IllegalArgumentException cuando el stock es null")
    void shouldPropagateIllegalArgumentExceptionWhenStockIsNull() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      Integer nullStock = null;

      UpdateProductStockRequest request = new UpdateProductStockRequest(nullStock);

      Product existingProduct =
          Product.builder().id(productId).name("Product").stock(50).branchId(branchId).build();

      given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));

      // Act & Assert
      assertThatThrownBy(() -> updateProductStockUseCase.execute(productId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Stock must be a positive number");

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
      UpdateProductStockRequest request = new UpdateProductStockRequest(100);

      // Act & Assert
      assertThatThrownBy(() -> updateProductStockUseCase.execute(null, request))
          .isInstanceOf(Exception.class);
    }
  }
}
