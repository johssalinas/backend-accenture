package com.accenture.franchise.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.application.dto.CreateProductRequest;
import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.mapper.DtoMapper;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.repository.BranchRepository;
import com.accenture.franchise.domain.repository.ProductRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas unitarias para {@link AddProductToBranchUseCase}. */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddProductToBranchUseCase - Pruebas Unitarias")
class AddProductToBranchUseCaseTest {

  @Mock private BranchRepository branchRepository;

  @Mock private ProductRepository productRepository;

  @Mock private DtoMapper mapper;

  @InjectMocks private AddProductToBranchUseCase addProductToBranchUseCase;

  @Nested
  @DisplayName("Casos de éxito")
  class SuccessCases {

    @Test
    @DisplayName("Debe agregar producto correctamente a sucursal existente")
    void shouldAddProductToBranchSuccessfully() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Hamburguesa Clásica";
      int stock = 50;

      CreateProductRequest request = new CreateProductRequest(productName, stock, branchId);

      UUID productId = UUID.randomUUID();
      Product savedProduct =
          Product.builder().id(productId).name(productName).stock(stock).branchId(branchId).build();

      ProductResponse expectedResponse = new ProductResponse(productId, productName, stock);

      given(branchRepository.existsById(branchId)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(productName, branchId)).willReturn(false);
      given(productRepository.save(any(Product.class))).willReturn(savedProduct);
      given(mapper.toProductResponse(savedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = addProductToBranchUseCase.execute(request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(productId);
      assertThat(result.name()).isEqualTo(productName);
      assertThat(result.stock()).isEqualTo(stock);

      verify(branchRepository).existsById(branchId);
      verify(productRepository).existsByNameAndBranchId(productName, branchId);
      verify(productRepository).save(any(Product.class));
      verify(mapper).toProductResponse(savedProduct);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000})
    @DisplayName("Debe crear producto con diferentes cantidades de stock válidas")
    void shouldCreateProductWithValidStockQuantities(int stock) {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Producto Test";

      CreateProductRequest request = new CreateProductRequest(productName, stock, branchId);

      Product savedProduct =
          Product.builder()
              .id(UUID.randomUUID())
              .name(productName)
              .stock(stock)
              .branchId(branchId)
              .build();

      ProductResponse expectedResponse =
          new ProductResponse(savedProduct.getId(), productName, stock);

      given(branchRepository.existsById(branchId)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(productName, branchId)).willReturn(false);
      given(productRepository.save(any(Product.class))).willReturn(savedProduct);
      given(mapper.toProductResponse(savedProduct)).willReturn(expectedResponse);

      // Act
      ProductResponse result = addProductToBranchUseCase.execute(request);

      // Assert
      assertThat(result.stock()).isEqualTo(stock);
      verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe capturar el producto guardado correctamente")
    void shouldCaptureTheSavedProductCorrectly() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Pizza Margarita";
      int stock = 25;

      CreateProductRequest request = new CreateProductRequest(productName, stock, branchId);

      Product savedProduct =
          Product.builder()
              .id(UUID.randomUUID())
              .name(productName)
              .stock(stock)
              .branchId(branchId)
              .build();

      ProductResponse expectedResponse =
          new ProductResponse(savedProduct.getId(), productName, stock);

      given(branchRepository.existsById(branchId)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(productName, branchId)).willReturn(false);
      given(productRepository.save(any(Product.class))).willReturn(savedProduct);
      given(mapper.toProductResponse(savedProduct)).willReturn(expectedResponse);

      // Act
      addProductToBranchUseCase.execute(request);

      // Assert
      ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
      verify(productRepository).save(productCaptor.capture());

      Product capturedProduct = productCaptor.getValue();
      assertThat(capturedProduct.getName()).isEqualTo(productName);
      assertThat(capturedProduct.getStock()).isEqualTo(stock);
      assertThat(capturedProduct.getBranchId()).isEqualTo(branchId);
    }
  }

  @Nested
  @DisplayName("Casos de error")
  class ErrorCases {

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando la sucursal no existe")
    void shouldThrowEntityNotFoundExceptionWhenBranchDoesNotExist() {
      // Arrange
      UUID nonExistentBranchId = UUID.randomUUID();
      String productName = "Producto Test";
      int stock = 10;

      CreateProductRequest request =
          new CreateProductRequest(productName, stock, nonExistentBranchId);

      given(branchRepository.existsById(nonExistentBranchId)).willReturn(false);

      // Act & Assert
      assertThatThrownBy(() -> addProductToBranchUseCase.execute(request))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Branch")
          .hasMessageContaining(nonExistentBranchId.toString());

      verify(branchRepository).existsById(nonExistentBranchId);
      verify(productRepository, never()).existsByNameAndBranchId(any(), any());
      verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar BusinessRuleViolationException cuando el nombre ya existe")
    void shouldThrowBusinessRuleViolationExceptionWhenNameAlreadyExists() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String duplicateName = "Producto Duplicado";
      int stock = 10;

      CreateProductRequest request = new CreateProductRequest(duplicateName, stock, branchId);

      given(branchRepository.existsById(branchId)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(duplicateName, branchId)).willReturn(true);

      // Act & Assert
      assertThatThrownBy(() -> addProductToBranchUseCase.execute(request))
          .isInstanceOf(BusinessRuleViolationException.class)
          .hasMessageContaining("Product with name already exists")
          .hasMessageContaining(duplicateName);

      verify(branchRepository).existsById(branchId);
      verify(productRepository).existsByNameAndBranchId(duplicateName, branchId);
      verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("No debe guardar si la validación de sucursal falla")
    void shouldNotSaveIfBranchValidationFails() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      CreateProductRequest request = new CreateProductRequest("Test Product", 10, branchId);

      given(branchRepository.existsById(branchId)).willReturn(false);

      // Act & Assert
      assertThatThrownBy(() -> addProductToBranchUseCase.execute(request))
          .isInstanceOf(EntityNotFoundException.class);

      verify(productRepository, never()).save(any(Product.class));
      verify(mapper, never()).toProductResponse(any(Product.class));
    }

    @Test
    @DisplayName("No debe guardar si la validación de nombre duplicado falla")
    void shouldNotSaveIfDuplicateNameValidationFails() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String duplicateName = "Duplicate";
      CreateProductRequest request = new CreateProductRequest(duplicateName, 10, branchId);

      given(branchRepository.existsById(branchId)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(duplicateName, branchId)).willReturn(true);

      // Act & Assert
      assertThatThrownBy(() -> addProductToBranchUseCase.execute(request))
          .isInstanceOf(BusinessRuleViolationException.class);

      verify(productRepository, never()).save(any(Product.class));
    }
  }

  @Nested
  @DisplayName("Verificación de interacciones")
  class InteractionTests {

    @Test
    @DisplayName("Debe validar sucursal antes de verificar nombre duplicado")
    void shouldValidateBranchBeforeCheckingDuplicateName() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Test Product";
      int stock = 10;

      CreateProductRequest request = new CreateProductRequest(productName, stock, branchId);

      Product savedProduct =
          Product.builder()
              .id(UUID.randomUUID())
              .name(productName)
              .stock(stock)
              .branchId(branchId)
              .build();

      ProductResponse expectedResponse =
          new ProductResponse(savedProduct.getId(), productName, stock);

      given(branchRepository.existsById(branchId)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(productName, branchId)).willReturn(false);
      given(productRepository.save(any(Product.class))).willReturn(savedProduct);
      given(mapper.toProductResponse(savedProduct)).willReturn(expectedResponse);

      // Act
      addProductToBranchUseCase.execute(request);

      // Assert
      var inOrder = org.mockito.Mockito.inOrder(branchRepository, productRepository, mapper);
      inOrder.verify(branchRepository).existsById(branchId);
      inOrder.verify(productRepository).existsByNameAndBranchId(productName, branchId);
      inOrder.verify(productRepository).save(any(Product.class));
      inOrder.verify(mapper).toProductResponse(savedProduct);
    }

    @Test
    @DisplayName("Debe guardar producto exactamente una vez")
    void shouldSaveProductExactlyOnce() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Test Product";
      int stock = 10;

      CreateProductRequest request = new CreateProductRequest(productName, stock, branchId);

      Product savedProduct =
          Product.builder()
              .id(UUID.randomUUID())
              .name(productName)
              .stock(stock)
              .branchId(branchId)
              .build();

      ProductResponse expectedResponse =
          new ProductResponse(savedProduct.getId(), productName, stock);

      given(branchRepository.existsById(branchId)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(productName, branchId)).willReturn(false);
      given(productRepository.save(any(Product.class))).willReturn(savedProduct);
      given(mapper.toProductResponse(savedProduct)).willReturn(expectedResponse);

      // Act
      addProductToBranchUseCase.execute(request);

      // Assert
      verify(productRepository, org.mockito.Mockito.times(1)).save(any(Product.class));
    }
  }

  @Nested
  @DisplayName("Validaciones de reglas de negocio")
  class BusinessRuleValidations {

    @Test
    @DisplayName("Debe permitir mismo nombre en diferentes sucursales")
    void shouldAllowSameNameInDifferentBranches() {
      // Arrange
      UUID branchId1 = UUID.randomUUID();
      UUID branchId2 = UUID.randomUUID();
      String sameProductName = "Hamburguesa Clásica";
      int stock = 10;

      CreateProductRequest request1 = new CreateProductRequest(sameProductName, stock, branchId1);
      CreateProductRequest request2 = new CreateProductRequest(sameProductName, stock, branchId2);

      Product savedProduct1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name(sameProductName)
              .stock(stock)
              .branchId(branchId1)
              .build();

      Product savedProduct2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name(sameProductName)
              .stock(stock)
              .branchId(branchId2)
              .build();

      ProductResponse response1 = new ProductResponse(savedProduct1.getId(), sameProductName, stock);
      ProductResponse response2 = new ProductResponse(savedProduct2.getId(), sameProductName, stock);

      // Primera sucursal
      given(branchRepository.existsById(branchId1)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(sameProductName, branchId1))
          .willReturn(false);
      given(productRepository.save(any(Product.class))).willReturn(savedProduct1);
      given(mapper.toProductResponse(savedProduct1)).willReturn(response1);

      // Act & Assert - Primer producto
      ProductResponse result1 = addProductToBranchUseCase.execute(request1);
      assertThat(result1.name()).isEqualTo(sameProductName);

      // Segunda sucursal
      given(branchRepository.existsById(branchId2)).willReturn(true);
      given(productRepository.existsByNameAndBranchId(sameProductName, branchId2))
          .willReturn(false);
      given(productRepository.save(any(Product.class))).willReturn(savedProduct2);
      given(mapper.toProductResponse(savedProduct2)).willReturn(response2);

      // Act & Assert - Segundo producto
      ProductResponse result2 = addProductToBranchUseCase.execute(request2);
      assertThat(result2.name()).isEqualTo(sameProductName);

      // Verificar que se guardaron dos productos
      verify(productRepository, org.mockito.Mockito.times(2)).save(any(Product.class));
    }
  }
}
