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

/** Pruebas unitarias para el modelo de dominio {@link Franchise}. */
@DisplayName("Franchise - Pruebas de Modelo de Dominio")
class FranchiseTest {

  @Nested
  @DisplayName("Constructor y Builder")
  class ConstructorAndBuilderTests {

    @Test
    @DisplayName("Debe crear franquicia con builder correctamente")
    void shouldCreateFranchiseWithBuilder() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String name = "McDonald's";
      List<Branch> branches = new ArrayList<>();

      // Act
      Franchise franchise =
          Franchise.builder().id(franchiseId).name(name).branches(branches).build();

      // Assert
      assertThat(franchise).isNotNull();
      assertThat(franchise.getId()).isEqualTo(franchiseId);
      assertThat(franchise.getName()).isEqualTo(name);
      assertThat(franchise.getBranches()).isEmpty();
    }

    @Test
    @DisplayName("Debe inicializar lista de sucursales vacía por defecto")
    void shouldInitializeEmptyBranchesListByDefault() {
      // Act
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Test").build();

      // Assert
      assertThat(franchise.getBranches()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Debe crear franquicia con NoArgsConstructor")
    void shouldCreateFranchiseWithNoArgsConstructor() {
      // Act
      Franchise franchise = new Franchise();

      // Assert
      assertThat(franchise).isNotNull();
    }
  }

  @Nested
  @DisplayName("addBranch - Agregar Sucursal")
  class AddBranchTests {

    private Franchise franchise;

    @BeforeEach
    void setUp() {
      franchise = Franchise.builder().id(UUID.randomUUID()).name("Test Franchise").build();
    }

    @Test
    @DisplayName("Debe agregar sucursal correctamente")
    void shouldAddBranchSuccessfully() {
      // Arrange
      Branch branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal Centro")
              .franchiseId(franchise.getId())
              .build();

      // Act
      franchise.addBranch(branch);

      // Assert
      assertThat(franchise.getBranches()).hasSize(1).contains(branch);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la sucursal es null")
    void shouldThrowExceptionWhenBranchIsNull() {
      // Act & Assert
      assertThatThrownBy(() -> franchise.addBranch(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Branch cannot be null");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el nombre de sucursal ya existe")
    void shouldThrowExceptionWhenBranchNameAlreadyExists() {
      // Arrange
      String duplicateName = "Sucursal Duplicada";
      Branch branch1 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name(duplicateName)
              .franchiseId(franchise.getId())
              .build();

      Branch branch2 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name(duplicateName)
              .franchiseId(franchise.getId())
              .build();

      franchise.addBranch(branch1);

      // Act & Assert
      assertThatThrownBy(() -> franchise.addBranch(branch2))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Branch name already exists in this franchise");
    }

    @Test
    @DisplayName("Debe permitir agregar múltiples sucursales con nombres diferentes")
    void shouldAllowAddingMultipleBranchesWithDifferentNames() {
      // Arrange
      Branch branch1 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal Centro")
              .franchiseId(franchise.getId())
              .build();

      Branch branch2 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal Norte")
              .franchiseId(franchise.getId())
              .build();

      // Act
      franchise.addBranch(branch1);
      franchise.addBranch(branch2);

      // Assert
      assertThat(franchise.getBranches()).hasSize(2).containsExactly(branch1, branch2);
    }
  }

  @Nested
  @DisplayName("updateName - Actualizar Nombre")
  class UpdateNameTests {

    private Franchise franchise;

    @BeforeEach
    void setUp() {
      franchise = Franchise.builder().id(UUID.randomUUID()).name("Original Name").build();
    }

    @Test
    @DisplayName("Debe actualizar nombre correctamente")
    void shouldUpdateNameSuccessfully() {
      // Arrange
      String newName = "Updated Name";

      // Act
      franchise.updateName(newName);

      // Assert
      assertThat(franchise.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Debe hacer trim del nombre actualizado")
    void shouldTrimUpdatedName() {
      // Arrange
      String nameWithSpaces = "  Updated Name  ";

      // Act
      franchise.updateName(nameWithSpaces);

      // Assert
      assertThat(franchise.getName()).isEqualTo("Updated Name");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Debe lanzar excepción cuando el nombre es nulo, vacío o solo espacios")
    void shouldThrowExceptionWhenNameIsNullOrBlank(String invalidName) {
      // Act & Assert
      assertThatThrownBy(() -> franchise.updateName(invalidName))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Franchise name cannot be blank");
    }

    @Test
    @DisplayName("Debe permitir actualizar nombre múltiples veces")
    void shouldAllowUpdatingNameMultipleTimes() {
      // Act & Assert
      franchise.updateName("Name 1");
      assertThat(franchise.getName()).isEqualTo("Name 1");

      franchise.updateName("Name 2");
      assertThat(franchise.getName()).isEqualTo("Name 2");

      franchise.updateName("Name 3");
      assertThat(franchise.getName()).isEqualTo("Name 3");
    }
  }

  @Nested
  @DisplayName("getTopStockProductsByBranch - Productos con Mayor Stock")
  class GetTopStockProductsByBranchTests {

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay sucursales")
    void shouldReturnEmptyListWhenNoBranches() {
      // Arrange
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Test").build();

      // Act
      List<ProductStock> result = franchise.getTopStockProductsByBranch();

      // Assert
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando las sucursales no tienen productos")
    void shouldReturnEmptyListWhenBranchesHaveNoProducts() {
      // Arrange
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Test").build();

      Branch branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal 1")
              .franchiseId(franchise.getId())
              .products(new ArrayList<>())
              .build();

      franchise.addBranch(branch);

      // Act
      List<ProductStock> result = franchise.getTopStockProductsByBranch();

      // Assert
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar productos con mayor stock por sucursal")
    void shouldReturnTopStockProductsPerBranch() {
      // Arrange
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Test").build();

      UUID branchId = UUID.randomUUID();
      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Product 1")
              .stock(10)
              .branchId(branchId)
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Product 2")
              .stock(50)
              .branchId(branchId)
              .build();

      List<Product> products = new ArrayList<>();
      products.add(product1);
      products.add(product2);

      Branch branch =
          Branch.builder()
              .id(branchId)
              .name("Sucursal 1")
              .franchiseId(franchise.getId())
              .products(products)
              .build();

      franchise.addBranch(branch);

      // Act
      List<ProductStock> result = franchise.getTopStockProductsByBranch();

      // Assert
      assertThat(result).hasSize(1);
      assertThat(result.getFirst().productName()).isEqualTo("Product 2");
      assertThat(result.getFirst().stock()).isEqualTo(50);
    }
  }

  @Nested
  @DisplayName("findBranchById - Buscar Sucursal por ID")
  class FindBranchByIdTests {

    @Test
    @DisplayName("Debe encontrar sucursal por ID existente")
    void shouldFindBranchByExistingId() {
      // Arrange
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Test").build();

      UUID branchId = UUID.randomUUID();
      Branch branch =
          Branch.builder()
              .id(branchId)
              .name("Sucursal Centro")
              .franchiseId(franchise.getId())
              .build();

      franchise.addBranch(branch);

      // Act
      Optional<Branch> result = franchise.findBranchById(branchId);

      // Assert
      assertThat(result).isPresent().contains(branch);
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando el ID no existe")
    void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
      // Arrange
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Test").build();

      Branch branch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal Centro")
              .franchiseId(franchise.getId())
              .build();

      franchise.addBranch(branch);

      UUID nonExistentId = UUID.randomUUID();

      // Act
      Optional<Branch> result = franchise.findBranchById(nonExistentId);

      // Assert
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando no hay sucursales")
    void shouldReturnEmptyOptionalWhenNoBranches() {
      // Arrange
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Test").build();

      // Act
      Optional<Branch> result = franchise.findBranchById(UUID.randomUUID());

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
      Franchise franchise = Franchise.builder().id(UUID.randomUUID()).name("Initial Name").build();

      // Act & Assert - Actualizar nombre
      franchise.updateName("Updated Name");
      assertThat(franchise.getName()).isEqualTo("Updated Name");

      // Act & Assert - Agregar sucursales
      Branch branch1 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Branch 1")
              .franchiseId(franchise.getId())
              .build();

      Branch branch2 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Branch 2")
              .franchiseId(franchise.getId())
              .build();

      franchise.addBranch(branch1);
      franchise.addBranch(branch2);

      assertThat(franchise.getBranches()).hasSize(2);

      // Act & Assert - Buscar sucursal
      Optional<Branch> foundBranch = franchise.findBranchById(branch1.getId());
      assertThat(foundBranch).isPresent().contains(branch1);
    }
  }
}
