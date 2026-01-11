package com.accenture.franchise.application.usecase.franchise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.ProductStockResponse;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.model.ProductStock;
import com.accenture.franchise.domain.repository.FranchiseRepository;
import java.util.List;
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
 * Pruebas unitarias para {@link GetTopStockProductsByFranchiseUseCase}.
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
@DisplayName("GetTopStockProductsByFranchiseUseCase - Pruebas Unitarias")
class GetTopStockProductsByFranchiseUseCaseTest {

  @Mock private FranchiseRepository franchiseRepository;
  @Mock private DtoMapper mapper;

  @InjectMocks private GetTopStockProductsByFranchiseUseCase getTopStockProductsByFranchiseUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe retornar lista de productos con más stock por sucursal")
    void shouldReturnTopStockProductsPerBranch() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      Branch branch1 =
          Branch.builder().id(branchId).name("Branch 1").franchiseId(franchiseId).build();

      Product product1 =
          Product.builder().id(productId).name("Product 1").stock(100).branchId(branchId).build();

      branch1.addProduct(product1);
      franchise.addBranch(branch1);

      ProductStock productStock =
          new ProductStock(productId, "Product 1", 100, branchId, "Branch 1");
      ProductStockResponse response1 =
          new ProductStockResponse(productId, "Product 1", 100, branchId, "Branch 1");

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toProductStockResponse(productStock)).willReturn(response1);

      // Act
      List<ProductStockResponse> result =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
      assertThat(result.get(0).productName()).isEqualTo("Product 1");
      assertThat(result.get(0).stock()).isEqualTo(100);

      verify(franchiseRepository).findById(franchiseId);
      verify(mapper).toProductStockResponse(productStock);
    }

    @Test
    @DisplayName("Debe retornar múltiples productos cuando hay múltiples sucursales")
    void shouldReturnMultipleProductsForMultipleBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branch1Id = UUID.randomUUID();
      UUID branch2Id = UUID.randomUUID();
      UUID product1Id = UUID.randomUUID();
      UUID product2Id = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      Branch branch1 =
          Branch.builder().id(branch1Id).name("Branch 1").franchiseId(franchiseId).build();

      Branch branch2 =
          Branch.builder().id(branch2Id).name("Branch 2").franchiseId(franchiseId).build();

      Product product1 =
          Product.builder().id(product1Id).name("Product 1").stock(100).branchId(branch1Id).build();

      Product product2 =
          Product.builder().id(product2Id).name("Product 2").stock(200).branchId(branch2Id).build();

      branch1.addProduct(product1);
      branch2.addProduct(product2);
      franchise.addBranch(branch1);
      franchise.addBranch(branch2);

      ProductStock productStock1 =
          new ProductStock(product1Id, "Product 1", 100, branch1Id, "Branch 1");
      ProductStock productStock2 =
          new ProductStock(product2Id, "Product 2", 200, branch2Id, "Branch 2");

      ProductStockResponse response1 =
          new ProductStockResponse(product1Id, "Product 1", 100, branch1Id, "Branch 1");

      ProductStockResponse response2 =
          new ProductStockResponse(product2Id, "Product 2", 200, branch2Id, "Branch 2");

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toProductStockResponse(productStock1)).willReturn(response1);
      given(mapper.toProductStockResponse(productStock2)).willReturn(response2);

      // Act
      List<ProductStockResponse> result =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result).hasSize(2);

      verify(franchiseRepository).findById(franchiseId);
      verify(mapper).toProductStockResponse(productStock1);
      verify(mapper).toProductStockResponse(productStock2);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando la franquicia no tiene sucursales")
    void shouldReturnEmptyListWhenFranchiseHasNoBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));

      // Act
      List<ProductStockResponse> result =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();

      verify(franchiseRepository).findById(franchiseId);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando las sucursales no tienen productos")
    void shouldReturnEmptyListWhenBranchesHaveNoProducts() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      Branch branch1 =
          Branch.builder().id(UUID.randomUUID()).name("Branch 1").franchiseId(franchiseId).build();

      franchise.addBranch(branch1);

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));

      // Act
      List<ProductStockResponse> result =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();

      verify(franchiseRepository).findById(franchiseId);
    }

    @Test
    @DisplayName("Debe mapear correctamente todos los campos del producto")
    void shouldMapAllProductFieldsCorrectly() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      Branch branch =
          Branch.builder().id(branchId).name("Branch Test").franchiseId(franchiseId).build();

      Product product =
          Product.builder()
              .id(productId)
              .name("Product Test")
              .stock(500)
              .branchId(branchId)
              .build();

      branch.addProduct(product);
      franchise.addBranch(branch);

      ProductStock productStock =
          new ProductStock(productId, "Product Test", 500, branchId, "Branch Test");

      ProductStockResponse response =
          new ProductStockResponse(productId, "Product Test", 500, branchId, "Branch Test");

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toProductStockResponse(productStock)).willReturn(response);

      // Act
      List<ProductStockResponse> result =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).hasSize(1);
      ProductStockResponse productResponse = result.get(0);
      assertThat(productResponse.branchId()).isEqualTo(branchId);
      assertThat(productResponse.branchName()).isEqualTo("Branch Test");
      assertThat(productResponse.productId()).isEqualTo(productId);
      assertThat(productResponse.productName()).isEqualTo("Product Test");
      assertThat(productResponse.stock()).isEqualTo(500);
    }

    @Test
    @DisplayName("Debe procesar correctamente franquicia con stock variado")
    void shouldProcessFranchiseWithVariedStockCorrectly() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      Branch branch =
          Branch.builder().id(branchId).name("Branch 1").franchiseId(franchiseId).build();

      Product highStock =
          Product.builder()
              .id(productId)
              .name("High Stock Product")
              .stock(1000)
              .branchId(branchId)
              .build();

      branch.addProduct(highStock);
      franchise.addBranch(branch);

      ProductStock productStock =
          new ProductStock(productId, "High Stock Product", 1000, branchId, "Branch 1");

      ProductStockResponse response =
          new ProductStockResponse(productId, "High Stock Product", 1000, branchId, "Branch 1");

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toProductStockResponse(productStock)).willReturn(response);

      // Act
      List<ProductStockResponse> result =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
      assertThat(result.get(0).stock()).isEqualTo(1000);
    }
  }

  @Nested
  @DisplayName("Casos de error")
  class ErrorCases {

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando la franquicia no existe")
    void shouldThrowEntityNotFoundExceptionWhenFranchiseNotFound() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      given(franchiseRepository.findById(nonExistentId)).willReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> getTopStockProductsByFranchiseUseCase.execute(nonExistentId))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Franchise")
          .hasMessageContaining(nonExistentId.toString());

      verify(franchiseRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Debe propagar excepciones del repositorio")
    void shouldPropagateRepositoryExceptions() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      RuntimeException repositoryException = new RuntimeException("Database error");

      given(franchiseRepository.findById(franchiseId)).willThrow(repositoryException);

      // Act & Assert
      assertThatThrownBy(() -> getTopStockProductsByFranchiseUseCase.execute(franchiseId))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database error");

      verify(franchiseRepository).findById(franchiseId);
    }

    @Test
    @DisplayName("Debe manejar errores en el mapper")
    void shouldHandleMapperErrors() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      Branch branch =
          Branch.builder().id(branchId).name("Branch 1").franchiseId(franchiseId).build();

      Product product =
          Product.builder().id(productId).name("Product 1").stock(100).branchId(branchId).build();

      branch.addProduct(product);
      franchise.addBranch(branch);

      ProductStock productStock =
          new ProductStock(productId, "Product 1", 100, branchId, "Branch 1");

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toProductStockResponse(productStock))
          .willThrow(new RuntimeException("Mapping error"));

      // Act & Assert
      assertThatThrownBy(() -> getTopStockProductsByFranchiseUseCase.execute(franchiseId))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Mapping error");

      verify(franchiseRepository).findById(franchiseId);
    }
  }

  @Nested
  @DisplayName("Casos de validación")
  class ValidationCases {

    @Test
    @DisplayName("Debe validar que el ID de franquicia no sea null")
    void shouldValidateFranchiseIdNotNull() {
      // Act & Assert
      assertThatThrownBy(() -> getTopStockProductsByFranchiseUseCase.execute(null))
          .isInstanceOf(Exception.class);
    }
  }

  @Nested
  @DisplayName("Casos de integración")
  class IntegrationCases {

    @Test
    @DisplayName("Debe manejar correctamente el flujo completo de obtención de productos")
    void shouldHandleCompleteFlowCorrectly() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      Branch branch =
          Branch.builder().id(branchId).name("Branch 1").franchiseId(franchiseId).build();

      Product product =
          Product.builder().id(productId).name("Product 1").stock(100).branchId(branchId).build();

      branch.addProduct(product);
      franchise.addBranch(branch);

      ProductStock productStock =
          new ProductStock(productId, "Product 1", 100, branchId, "Branch 1");

      ProductStockResponse response =
          new ProductStockResponse(productId, "Product 1", 100, branchId, "Branch 1");

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));
      given(mapper.toProductStockResponse(productStock)).willReturn(response);

      // Act
      List<ProductStockResponse> result =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
      assertThat(result.get(0)).isNotNull();

      verify(franchiseRepository).findById(franchiseId);
      verify(mapper).toProductStockResponse(productStock);
    }

    @Test
    @DisplayName("Debe retornar lista inmutable o nueva instancia")
    void shouldReturnNewListInstance() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Franchise Test").build();

      given(franchiseRepository.findById(franchiseId)).willReturn(Optional.of(franchise));

      // Act
      List<ProductStockResponse> result1 =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);
      List<ProductStockResponse> result2 =
          getTopStockProductsByFranchiseUseCase.execute(franchiseId);

      // Assert - verificar que son instancias diferentes
      assertThat(result1).isNotSameAs(result2);
    }
  }
}
