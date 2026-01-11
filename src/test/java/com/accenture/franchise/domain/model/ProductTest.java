package com.accenture.franchise.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Pruebas unitarias para el modelo de dominio {@link Product}. */
@DisplayName("Product - Pruebas de Modelo de Dominio")
class ProductTest {

  @Nested
  @DisplayName("Constructor y Builder")
  class ConstructorAndBuilderTests {

    @Test
    @DisplayName("Debe crear producto con builder correctamente")
    void shouldCreateProductWithBuilder() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String name = "Hamburguesa";
      Integer stock = 50;

      // Act
      Product product =
          Product.builder().id(productId).name(name).stock(stock).branchId(branchId).build();

      // Assert
      assertThat(product).isNotNull();
      assertThat(product.getId()).isEqualTo(productId);
      assertThat(product.getName()).isEqualTo(name);
      assertThat(product.getStock()).isEqualTo(stock);
      assertThat(product.getBranchId()).isEqualTo(branchId);
    }

    @Test
    @DisplayName("Debe crear producto con NoArgsConstructor")
    void shouldCreateProductWithNoArgsConstructor() {
      // Act
      Product product = new Product();

      // Assert
      assertThat(product).isNotNull();
    }

    @Test
    @DisplayName("Debe permitir modificar branchId con setter")
    void shouldAllowModifyingBranchIdWithSetter() {
      // Arrange
      Product product = Product.builder().name("Test").stock(10).build();
      UUID newBranchId = UUID.randomUUID();

      // Act
      product.setBranchId(newBranchId);

      // Assert
      assertThat(product.getBranchId()).isEqualTo(newBranchId);
    }
  }

  @Nested
  @DisplayName("updateStock - Actualizar Stock")
  class UpdateStockTests {

    private Product product;

    @BeforeEach
    void setUp() {
      product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Test Product")
              .stock(100)
              .branchId(UUID.randomUUID())
              .build();
    }

    @Test
    @DisplayName("Debe actualizar stock correctamente con valor positivo")
    void shouldUpdateStockSuccessfullyWithPositiveValue() {
      // Arrange
      Integer newStock = 200;

      // Act
      product.updateStock(newStock);

      // Assert
      assertThat(product.getStock()).isEqualTo(newStock);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000, 9999})
    @DisplayName("Debe actualizar stock con diferentes valores válidos")
    void shouldUpdateStockWithValidValues(Integer newStock) {
      // Act
      product.updateStock(newStock);

      // Assert
      assertThat(product.getStock()).isEqualTo(newStock);
    }

    @Test
    @DisplayName("Debe permitir stock cero")
    void shouldAllowZeroStock() {
      // Act
      product.updateStock(0);

      // Assert
      assertThat(product.getStock()).isZero();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el stock es nulo")
    void shouldThrowExceptionWhenStockIsNull() {
      // Act & Assert
      assertThatThrownBy(() -> product.updateStock(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Stock must be a positive number");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100, Integer.MIN_VALUE})
    @DisplayName("Debe lanzar excepción cuando el stock es negativo")
    void shouldThrowExceptionWhenStockIsNegative(Integer negativeStock) {
      // Act & Assert
      assertThatThrownBy(() -> product.updateStock(negativeStock))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Stock must be a positive number");
    }

    @Test
    @DisplayName("Debe permitir actualizar stock múltiples veces")
    void shouldAllowUpdatingStockMultipleTimes() {
      // Act & Assert
      product.updateStock(50);
      assertThat(product.getStock()).isEqualTo(50);

      product.updateStock(75);
      assertThat(product.getStock()).isEqualTo(75);

      product.updateStock(25);
      assertThat(product.getStock()).isEqualTo(25);
    }

    @Test
    @DisplayName("Debe manejar actualización a stock máximo")
    void shouldHandleUpdateToMaxStock() {
      // Arrange
      Integer maxStock = Integer.MAX_VALUE;

      // Act
      product.updateStock(maxStock);

      // Assert
      assertThat(product.getStock()).isEqualTo(maxStock);
    }
  }

  @Nested
  @DisplayName("updateName - Actualizar Nombre")
  class UpdateNameTests {

    private Product product;

    @BeforeEach
    void setUp() {
      product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Original Name")
              .stock(100)
              .branchId(UUID.randomUUID())
              .build();
    }

    @Test
    @DisplayName("Debe actualizar nombre correctamente")
    void shouldUpdateNameSuccessfully() {
      // Arrange
      String newName = "Updated Product Name";

      // Act
      product.updateName(newName);

      // Assert
      assertThat(product.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Debe hacer trim del nombre actualizado")
    void shouldTrimUpdatedName() {
      // Arrange
      String nameWithSpaces = "  Updated Name  ";

      // Act
      product.updateName(nameWithSpaces);

      // Assert
      assertThat(product.getName()).isEqualTo("Updated Name");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n", "   \t\n   "})
    @DisplayName("Debe lanzar excepción cuando el nombre es nulo, vacío o solo espacios")
    void shouldThrowExceptionWhenNameIsNullOrBlank(String invalidName) {
      // Act & Assert
      assertThatThrownBy(() -> product.updateName(invalidName))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Product name cannot be blank");
    }

    @Test
    @DisplayName("Debe permitir actualizar nombre múltiples veces")
    void shouldAllowUpdatingNameMultipleTimes() {
      // Act & Assert
      product.updateName("Name 1");
      assertThat(product.getName()).isEqualTo("Name 1");

      product.updateName("Name 2");
      assertThat(product.getName()).isEqualTo("Name 2");

      product.updateName("Name 3");
      assertThat(product.getName()).isEqualTo("Name 3");
    }

    @Test
    @DisplayName("Debe permitir nombres con caracteres especiales")
    void shouldAllowNamesWithSpecialCharacters() {
      // Arrange
      String specialName = "Producto #1 - Súper Ofertas (50% OFF)";

      // Act
      product.updateName(specialName);

      // Assert
      assertThat(product.getName()).isEqualTo(specialName);
    }

    @Test
    @DisplayName("Debe permitir nombres largos")
    void shouldAllowLongNames() {
      // Arrange
      String longName = "A".repeat(200);

      // Act
      product.updateName(longName);

      // Assert
      assertThat(product.getName()).hasSize(200);
    }
  }

  @Nested
  @DisplayName("Integración - Escenarios Complejos")
  class IntegrationTests {

    @Test
    @DisplayName("Debe manejar múltiples operaciones en secuencia")
    void shouldHandleMultipleOperationsInSequence() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Initial Product")
              .stock(50)
              .branchId(UUID.randomUUID())
              .build();

      // Act & Assert - Actualizar nombre
      product.updateName("Updated Product");
      assertThat(product.getName()).isEqualTo("Updated Product");

      // Act & Assert - Actualizar stock
      product.updateStock(100);
      assertThat(product.getStock()).isEqualTo(100);

      // Act & Assert - Actualizar nombre con trim
      product.updateName("  Final Name  ");
      assertThat(product.getName()).isEqualTo("Final Name");

      // Act & Assert - Reducir stock
      product.updateStock(25);
      assertThat(product.getStock()).isEqualTo(25);
    }

    @Test
    @DisplayName("Debe manejar cambio de sucursal manteniendo otras propiedades")
    void shouldHandleBranchChangeWhileMaintainingOtherProperties() {
      // Arrange
      UUID initialBranchId = UUID.randomUUID();
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Test Product")
              .stock(50)
              .branchId(initialBranchId)
              .build();

      UUID newBranchId = UUID.randomUUID();

      // Act
      product.setBranchId(newBranchId);

      // Assert
      assertThat(product.getBranchId()).isEqualTo(newBranchId);
      assertThat(product.getName()).isEqualTo("Test Product");
      assertThat(product.getStock()).isEqualTo(50);
    }

    @Test
    @DisplayName("Debe validar independencia de operaciones de actualización")
    void shouldValidateIndependenceOfUpdateOperations() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Product")
              .stock(100)
              .branchId(UUID.randomUUID())
              .build();

      // Act - Actualizar stock no afecta nombre
      product.updateStock(200);

      // Assert
      assertThat(product.getStock()).isEqualTo(200);
      assertThat(product.getName()).isEqualTo("Product");

      // Act - Actualizar nombre no afecta stock
      product.updateName("New Name");

      // Assert
      assertThat(product.getName()).isEqualTo("New Name");
      assertThat(product.getStock()).isEqualTo(200);
    }
  }

  @Nested
  @DisplayName("Casos extremos")
  class EdgeCaseTests {

    @Test
    @DisplayName("Debe manejar stock en límite superior")
    void shouldHandleStockAtUpperBoundary() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Test")
              .stock(0)
              .branchId(UUID.randomUUID())
              .build();

      // Act
      product.updateStock(Integer.MAX_VALUE);

      // Assert
      assertThat(product.getStock()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("Debe manejar nombre con un solo carácter")
    void shouldHandleNameWithSingleCharacter() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Test")
              .stock(10)
              .branchId(UUID.randomUUID())
              .build();

      // Act
      product.updateName("A");

      // Assert
      assertThat(product.getName()).isEqualTo("A");
    }

    @Test
    @DisplayName("Debe manejar nombre con espacios múltiples internos")
    void shouldHandleNameWithMultipleInternalSpaces() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Test")
              .stock(10)
              .branchId(UUID.randomUUID())
              .build();

      String nameWithSpaces = "Product    With    Many    Spaces";

      // Act
      product.updateName(nameWithSpaces);

      // Assert - Los espacios internos se mantienen, solo se hace trim de extremos
      assertThat(product.getName()).isEqualTo(nameWithSpaces);
    }
  }
}
