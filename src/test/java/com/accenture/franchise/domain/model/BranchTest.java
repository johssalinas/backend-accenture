package com.accenture.franchise.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Pruebas unitarias para el modelo de dominio {@link Branch}. */
@DisplayName("Branch - Pruebas de Modelo de Dominio")
class BranchTest {

  @Nested
  @DisplayName("Constructor y Builder")
  class ConstructorAndBuilderTests {

    @Test
    @DisplayName("Debe crear sucursal con builder correctamente")
    void shouldCreateBranchWithBuilder() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      String name = "Sucursal Centro";
      List<Product> products = new ArrayList<>();

      // Act
      Branch branch =
          Branch.builder()
              .id(branchId)
              .name(name)
              .franchiseId(franchiseId)
              .products(products)
              .build();

      // Assert
      assertThat(branch).isNotNull();
      assertThat(branch.getId()).isEqualTo(branchId);
      assertThat(branch.getName()).isEqualTo(name);
      assertThat(branch.getFranchiseId()).isEqualTo(franchiseId);
      assertThat(branch.getProducts()).isEmpty();
    }

    @Test
    @DisplayName("Debe inicializar lista de productos vacía por defecto")
    void shouldInitializeEmptyProductsListByDefault() {
      // Act
      Branch branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Test")
              .franchiseId(UUID.randomUUID())
              .build();

      // Assert
      assertThat(branch.getProducts()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Debe crear sucursal con NoArgsConstructor")
    void shouldCreateBranchWithNoArgsConstructor() {
      // Act
      Branch branch = new Branch();

      // Assert
      assertThat(branch).isNotNull();
    }
  }

  @Nested
  @DisplayName("addProduct - Agregar Producto")
  class AddProductTests {

    private Branch branch;

    @BeforeEach
    void setUp() {
      branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Test Branch")
              .franchiseId(UUID.randomUUID())
              .build();
    }

    @Test
    @DisplayName("Debe agregar producto correctamente")
    void shouldAddProductSuccessfully() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      // Act
      branch.addProduct(product);

      // Assert
      assertThat(branch.getProducts()).hasSize(1).contains(product);
      assertThat(product.getBranchId()).isEqualTo(branch.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto es null")
    void shouldThrowExceptionWhenProductIsNull() {
      // Act & Assert
      assertThatThrownBy(() -> branch.addProduct(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Product cannot be null");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el nombre de producto ya existe")
    void shouldThrowExceptionWhenProductNameAlreadyExists() {
      // Arrange
      String duplicateName = "Producto Duplicado";
      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name(duplicateName)
              .stock(10)
              .branchId(branch.getId())
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name(duplicateName)
              .stock(20)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product1);

      // Act & Assert
      assertThatThrownBy(() -> branch.addProduct(product2))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Product name already exists in this branch");
    }

    @Test
    @DisplayName("Debe permitir agregar múltiples productos con nombres diferentes")
    void shouldAllowAddingMultipleProductsWithDifferentNames() {
      // Arrange
      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Pizza")
              .stock(30)
              .branchId(branch.getId())
              .build();

      // Act
      branch.addProduct(product1);
      branch.addProduct(product2);

      // Assert
      assertThat(branch.getProducts()).hasSize(2).containsExactly(product1, product2);
    }
  }

  @Nested
  @DisplayName("removeProduct - Eliminar Producto")
  class RemoveProductTests {

    private Branch branch;

    @BeforeEach
    void setUp() {
      branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Test Branch")
              .franchiseId(UUID.randomUUID())
              .build();
    }

    @Test
    @DisplayName("Debe eliminar producto correctamente")
    void shouldRemoveProductSuccessfully() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product);
      assertThat(branch.getProducts()).hasSize(1);

      // Act
      branch.removeProduct(product.getId());

      // Assert
      assertThat(branch.getProducts()).isEmpty();
    }

    @Test
    @DisplayName("Debe manejar eliminación de producto inexistente sin lanzar excepción")
    void shouldHandleRemovalOfNonExistentProductWithoutException() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      // Act & Assert - no debe lanzar excepción
      branch.removeProduct(nonExistentId);
      assertThat(branch.getProducts()).isEmpty();
    }

    @Test
    @DisplayName("Debe eliminar producto específico entre múltiples productos")
    void shouldRemoveSpecificProductAmongMultipleProducts() {
      // Arrange
      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Pizza")
              .stock(30)
              .branchId(branch.getId())
              .build();

      Product product3 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Tacos")
              .stock(40)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product1);
      branch.addProduct(product2);
      branch.addProduct(product3);

      // Act
      branch.removeProduct(product2.getId());

      // Assert
      assertThat(branch.getProducts()).hasSize(2).containsExactly(product1, product3);
    }
  }

  @Nested
  @DisplayName("updateName - Actualizar Nombre")
  class UpdateNameTests {

    private Branch branch;

    @BeforeEach
    void setUp() {
      branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Nombre Original")
              .franchiseId(UUID.randomUUID())
              .build();
    }

    @Test
    @DisplayName("Debe actualizar nombre correctamente")
    void shouldUpdateNameSuccessfully() {
      // Arrange
      String newName = "Nombre Actualizado";

      // Act
      branch.updateName(newName);

      // Assert
      assertThat(branch.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Debe hacer trim del nombre actualizado")
    void shouldTrimUpdatedName() {
      // Arrange
      String newName = "  Nombre con Espacios  ";

      // Act
      branch.updateName(newName);

      // Assert
      assertThat(branch.getName()).isEqualTo("Nombre con Espacios");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Debe lanzar excepción cuando el nombre es nulo, vacío o solo espacios")
    void shouldThrowExceptionWhenNameIsNullOrBlank(String invalidName) {
      // Act & Assert
      assertThatThrownBy(() -> branch.updateName(invalidName))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Branch name cannot be blank");
    }

    @Test
    @DisplayName("Debe permitir actualizar nombre múltiples veces")
    void shouldAllowUpdatingNameMultipleTimes() {
      // Act
      branch.updateName("Primer Nombre");
      branch.updateName("Segundo Nombre");
      branch.updateName("Tercer Nombre");

      // Assert
      assertThat(branch.getName()).isEqualTo("Tercer Nombre");
    }
  }

  @Nested
  @DisplayName("getProductWithMostStock - Producto con Mayor Stock")
  class GetProductWithMostStockTests {

    private Branch branch;

    @BeforeEach
    void setUp() {
      branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Test Branch")
              .franchiseId(UUID.randomUUID())
              .build();
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando no hay productos")
    void shouldReturnEmptyOptionalWhenNoProducts() {
      // Act
      Optional<ProductStock> result = branch.getProductWithMostStock();

      // Assert
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar el único producto cuando solo hay uno")
    void shouldReturnSingleProductWhenOnlyOne() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product);

      // Act
      Optional<ProductStock> result = branch.getProductWithMostStock();

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get().productName()).isEqualTo("Hamburguesa");
      assertThat(result.get().stock()).isEqualTo(50);
      assertThat(result.get().branchId()).isEqualTo(branch.getId());
      assertThat(result.get().branchName()).isEqualTo(branch.getName());
    }

    @Test
    @DisplayName("Debe retornar el producto con mayor stock entre múltiples")
    void shouldReturnProductWithMostStockAmongMultiple() {
      // Arrange
      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Pizza")
              .stock(100)
              .branchId(branch.getId())
              .build();

      Product product3 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Tacos")
              .stock(75)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product1);
      branch.addProduct(product2);
      branch.addProduct(product3);

      // Act
      Optional<ProductStock> result = branch.getProductWithMostStock();

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get().productName()).isEqualTo("Pizza");
      assertThat(result.get().stock()).isEqualTo(100);
    }

    @Test
    @DisplayName("Debe retornar uno de los productos cuando hay empate en stock")
    void shouldReturnOneProductWhenTiedStock() {
      // Arrange
      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Pizza")
              .stock(50)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product1);
      branch.addProduct(product2);

      // Act
      Optional<ProductStock> result = branch.getProductWithMostStock();

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get().stock()).isEqualTo(50);
      assertThat(result.get().productName()).isIn("Hamburguesa", "Pizza");
    }
  }

  @Nested
  @DisplayName("findProductById - Buscar Producto por ID")
  class FindProductByIdTests {

    private Branch branch;

    @BeforeEach
    void setUp() {
      branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Test Branch")
              .franchiseId(UUID.randomUUID())
              .build();
    }

    @Test
    @DisplayName("Debe encontrar producto por ID existente")
    void shouldFindProductByExistingId() {
      // Arrange
      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Pizza")
              .stock(30)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product1);
      branch.addProduct(product2);

      // Act
      Optional<Product> result = branch.findProductById(product2.getId());

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(product2);
      assertThat(result.get().getName()).isEqualTo("Pizza");
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando el ID no existe")
    void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
      // Arrange
      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branch.getId())
              .build();

      branch.addProduct(product);
      UUID nonExistentId = UUID.randomUUID();

      // Act
      Optional<Product> result = branch.findProductById(nonExistentId);

      // Assert
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando no hay productos")
    void shouldReturnEmptyOptionalWhenNoProducts() {
      // Arrange
      UUID anyId = UUID.randomUUID();

      // Act
      Optional<Product> result = branch.findProductById(anyId);

      // Assert
      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("Integración - Escenarios Complejos")
  class IntegrationTests {

    @Test
    @DisplayName("Debe manejar múltiples operaciones en secuencia")
    void shouldHandleMultipleOperationsInSequence() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      Branch branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal Original")
              .franchiseId(franchiseId)
              .build();

      // Act & Assert - Agregar productos
      Product product1 =
          Product.builder().id(UUID.randomUUID()).name("Hamburguesa").stock(50).build();

      Product product2 = Product.builder().id(UUID.randomUUID()).name("Pizza").stock(100).build();

      branch.addProduct(product1);
      branch.addProduct(product2);
      assertThat(branch.getProducts()).hasSize(2);

      // Act & Assert - Actualizar nombre
      branch.updateName("Sucursal Actualizada");
      assertThat(branch.getName()).isEqualTo("Sucursal Actualizada");

      // Act & Assert - Obtener producto con más stock
      Optional<ProductStock> topProduct = branch.getProductWithMostStock();
      assertThat(topProduct).isPresent();
      assertThat(topProduct.get().productName()).isEqualTo("Pizza");
      assertThat(topProduct.get().stock()).isEqualTo(100);

      // Act & Assert - Eliminar producto
      branch.removeProduct(product1.getId());
      assertThat(branch.getProducts()).hasSize(1);

      // Act & Assert - Verificar que el producto con más stock cambió
      Optional<ProductStock> newTopProduct = branch.getProductWithMostStock();
      assertThat(newTopProduct).isPresent();
      assertThat(newTopProduct.get().productName()).isEqualTo("Pizza");
    }

    @Test
    @DisplayName("Debe mantener integridad de datos durante operaciones complejas")
    void shouldMaintainDataIntegrityDuringComplexOperations() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Test").franchiseId(franchiseId).build();

      // Agregar 5 productos
      for (int i = 1; i <= 5; i++) {
        Product product =
            Product.builder().id(UUID.randomUUID()).name("Producto " + i).stock(i * 10).build();
        branch.addProduct(product);
      }

      assertThat(branch.getProducts()).hasSize(5);

      // Eliminar 2 productos
      List<Product> products = branch.getProducts();
      branch.removeProduct(products.get(0).getId());
      branch.removeProduct(products.get(1).getId());

      assertThat(branch.getProducts()).hasSize(3);

      // Verificar que los branchId se asignaron correctamente
      branch.getProducts().forEach(p -> assertThat(p.getBranchId()).isEqualTo(branchId));

      // Verificar producto con más stock
      Optional<ProductStock> topProduct = branch.getProductWithMostStock();
      assertThat(topProduct).isPresent();
      assertThat(topProduct.get().stock()).isEqualTo(50);
      assertThat(topProduct.get().branchName()).isEqualTo("Sucursal Test");
    }
  }
}
