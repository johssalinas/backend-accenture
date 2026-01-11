package com.accenture.franchise.application.dto.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.ProductStockResponse;
import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.domain.model.ProductStock;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para {@link DtoMapper}.
 *
 * <p>Siguiendo mejores prácticas:
 *
 * <ul>
 *   <li>Tests independientes sin mocks (mapper puro)
 *   <li>Patrón AAA (Arrange-Act-Assert)
 *   <li>Verificación de mapeo completo de campos
 *   <li>Tests organizados con @Nested para agrupar casos relacionados
 * </ul>
 */
@DisplayName("DtoMapper - Pruebas Unitarias")
class DtoMapperTest {

  private DtoMapper dtoMapper;

  @BeforeEach
  void setUp() {
    dtoMapper = new DtoMapper();
  }

  @Nested
  @DisplayName("toFranchiseResponse - Mapeo de Franchise")
  class ToFranchiseResponseTests {

    @Test
    @DisplayName("Debe mapear correctamente una franquicia con sucursales")
    void shouldMapFranchiseWithBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      Franchise franchise = Franchise.builder().id(franchiseId).name("McDonald's").build();
      franchise.addBranch(branch);

      // Act
      FranchiseResponse response = dtoMapper.toFranchiseResponse(franchise);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(franchiseId);
      assertThat(response.name()).isEqualTo("McDonald's");
      assertThat(response.branches()).isNotNull();
      assertThat(response.branches()).hasSize(1);
      assertThat(response.branches().get(0).id()).isEqualTo(branchId);
      assertThat(response.branches().get(0).name()).isEqualTo("Sucursal Centro");
      assertThat(response.branches().get(0).products()).isEmpty();
    }

    @Test
    @DisplayName("Debe mapear correctamente una franquicia sin sucursales")
    void shouldMapFranchiseWithoutBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      Franchise franchise = Franchise.builder().id(franchiseId).name("Burger King").build();

      // Act
      FranchiseResponse response = dtoMapper.toFranchiseResponse(franchise);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(franchiseId);
      assertThat(response.name()).isEqualTo("Burger King");
      assertThat(response.branches()).isNull();
    }

    @Test
    @DisplayName("Debe retornar null cuando la franquicia es null")
    void shouldReturnNullWhenFranchiseIsNull() {
      // Act
      FranchiseResponse response = dtoMapper.toFranchiseResponse(null);

      // Assert
      assertThat(response).isNull();
    }

    @Test
    @DisplayName("Debe mapear múltiples sucursales sin incluir productos")
    void shouldMapMultipleBranchesWithoutProducts() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Branch branch1 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal Norte")
              .franchiseId(franchiseId)
              .build();

      Branch branch2 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal Sur")
              .franchiseId(franchiseId)
              .build();

      Franchise franchise = Franchise.builder().id(franchiseId).name("KFC").build();
      franchise.addBranch(branch1);
      franchise.addBranch(branch2);

      // Act
      FranchiseResponse response = dtoMapper.toFranchiseResponse(franchise);

      // Assert
      assertThat(response.branches()).hasSize(2);
      assertThat(response.branches().get(0).products()).isEmpty();
      assertThat(response.branches().get(1).products()).isEmpty();
    }
  }

  @Nested
  @DisplayName("toBranchResponseWithoutProducts - Mapeo de Branch sin productos")
  class ToBranchResponseWithoutProductsTests {

    @Test
    @DisplayName("Debe mapear correctamente una sucursal sin incluir productos")
    void shouldMapBranchWithoutProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      Product product =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branchId)
              .build();
      branch.addProduct(product);

      // Act
      BranchResponse response = dtoMapper.toBranchResponseWithoutProducts(branch);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(branchId);
      assertThat(response.name()).isEqualTo("Sucursal Centro");
      assertThat(response.franchiseId()).isEqualTo(franchiseId);
      assertThat(response.products()).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar null cuando la sucursal es null")
    void shouldReturnNullWhenBranchIsNull() {
      // Act
      BranchResponse response = dtoMapper.toBranchResponseWithoutProducts(null);

      // Assert
      assertThat(response).isNull();
    }

    @Test
    @DisplayName("Debe mapear sucursal con lista de productos vacía")
    void shouldMapBranchWithEmptyProductList() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Este").franchiseId(franchiseId).build();

      // Act
      BranchResponse response = dtoMapper.toBranchResponseWithoutProducts(branch);

      // Assert
      assertThat(response.products()).isEmpty();
    }
  }

  @Nested
  @DisplayName("toBranchResponse - Mapeo de Branch con productos")
  class ToBranchResponseTests {

    @Test
    @DisplayName("Debe mapear correctamente una sucursal con productos")
    void shouldMapBranchWithProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      Product product =
          Product.builder().id(productId).name("Hamburguesa").stock(50).branchId(branchId).build();
      branch.addProduct(product);

      // Act
      BranchResponse response = dtoMapper.toBranchResponse(branch);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(branchId);
      assertThat(response.name()).isEqualTo("Sucursal Centro");
      assertThat(response.franchiseId()).isEqualTo(franchiseId);
      assertThat(response.products()).hasSize(1);
      assertThat(response.products().get(0).id()).isEqualTo(productId);
      assertThat(response.products().get(0).name()).isEqualTo("Hamburguesa");
      assertThat(response.products().get(0).stock()).isEqualTo(50);
    }

    @Test
    @DisplayName("Debe mapear correctamente una sucursal sin productos")
    void shouldMapBranchWithoutProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Norte").franchiseId(franchiseId).build();

      // Act
      BranchResponse response = dtoMapper.toBranchResponse(branch);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.products()).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar null cuando la sucursal es null")
    void shouldReturnNullWhenBranchIsNull() {
      // Act
      BranchResponse response = dtoMapper.toBranchResponse(null);

      // Assert
      assertThat(response).isNull();
    }

    @Test
    @DisplayName("Debe mapear múltiples productos correctamente")
    void shouldMapMultipleProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Sur").franchiseId(franchiseId).build();

      Product product1 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branchId)
              .build();

      Product product2 =
          Product.builder()
              .id(UUID.randomUUID())
              .name("Papas Fritas")
              .stock(100)
              .branchId(branchId)
              .build();

      branch.addProduct(product1);
      branch.addProduct(product2);

      // Act
      BranchResponse response = dtoMapper.toBranchResponse(branch);

      // Assert
      assertThat(response.products()).hasSize(2);
      assertThat(response.products().get(0).name()).isEqualTo("Hamburguesa");
      assertThat(response.products().get(1).name()).isEqualTo("Papas Fritas");
    }
  }

  @Nested
  @DisplayName("toProductResponse - Mapeo de Product")
  class ToProductResponseTests {

    @Test
    @DisplayName("Debe mapear correctamente un producto")
    void shouldMapProduct() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder().id(productId).name("Hamburguesa").stock(50).branchId(branchId).build();

      // Act
      ProductResponse response = dtoMapper.toProductResponse(product);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(productId);
      assertThat(response.name()).isEqualTo("Hamburguesa");
      assertThat(response.stock()).isEqualTo(50);
      assertThat(response.branchId()).isEqualTo(branchId);
    }

    @Test
    @DisplayName("Debe retornar null cuando el producto es null")
    void shouldReturnNullWhenProductIsNull() {
      // Act
      ProductResponse response = dtoMapper.toProductResponse(null);

      // Assert
      assertThat(response).isNull();
    }

    @Test
    @DisplayName("Debe mapear producto con stock cero")
    void shouldMapProductWithZeroStock() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder()
              .id(productId)
              .name("Producto Agotado")
              .stock(0)
              .branchId(branchId)
              .build();

      // Act
      ProductResponse response = dtoMapper.toProductResponse(product);

      // Assert
      assertThat(response.stock()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe mapear producto con stock grande")
    void shouldMapProductWithLargeStock() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder()
              .id(productId)
              .name("Producto Popular")
              .stock(999999)
              .branchId(branchId)
              .build();

      // Act
      ProductResponse response = dtoMapper.toProductResponse(product);

      // Assert
      assertThat(response.stock()).isEqualTo(999999);
    }

    @Test
    @DisplayName("Debe mapear correctamente todos los campos del producto")
    void shouldMapAllProductFields() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder()
              .id(productId)
              .name("Pizza Especial")
              .stock(25)
              .branchId(branchId)
              .build();

      // Act
      ProductResponse response = dtoMapper.toProductResponse(product);

      // Assert
      assertThat(response.id()).isNotNull();
      assertThat(response.name()).isNotNull();
      assertThat(response.stock()).isNotNull();
      assertThat(response.branchId()).isNotNull();
    }
  }

  @Nested
  @DisplayName("toProductStockResponse - Mapeo de ProductStock")
  class ToProductStockResponseTests {

    @Test
    @DisplayName("Debe mapear correctamente un ProductStock")
    void shouldMapProductStock() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      ProductStock productStock =
          new ProductStock(productId, "Hamburguesa", 50, branchId, "Sucursal Centro");

      // Act
      ProductStockResponse response = dtoMapper.toProductStockResponse(productStock);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.productId()).isEqualTo(productId);
      assertThat(response.productName()).isEqualTo("Hamburguesa");
      assertThat(response.stock()).isEqualTo(50);
      assertThat(response.branchId()).isEqualTo(branchId);
      assertThat(response.branchName()).isEqualTo("Sucursal Centro");
    }

    @Test
    @DisplayName("Debe retornar null cuando el ProductStock es null")
    void shouldReturnNullWhenProductStockIsNull() {
      // Act
      ProductStockResponse response = dtoMapper.toProductStockResponse(null);

      // Assert
      assertThat(response).isNull();
    }

    @Test
    @DisplayName("Debe mapear ProductStock con todos los campos")
    void shouldMapAllProductStockFields() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      ProductStock productStock =
          new ProductStock(productId, "Producto Test", 100, branchId, "Branch Test");

      // Act
      ProductStockResponse response = dtoMapper.toProductStockResponse(productStock);

      // Assert
      assertThat(response.productId()).isNotNull();
      assertThat(response.productName()).isNotNull();
      assertThat(response.stock()).isNotNull();
      assertThat(response.branchId()).isNotNull();
      assertThat(response.branchName()).isNotNull();
    }

    @Test
    @DisplayName("Debe mapear ProductStock con stock cero")
    void shouldMapProductStockWithZeroStock() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      ProductStock productStock =
          new ProductStock(productId, "Producto Agotado", 0, branchId, "Sucursal Norte");

      // Act
      ProductStockResponse response = dtoMapper.toProductStockResponse(productStock);

      // Assert
      assertThat(response.stock()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe preservar nombres con caracteres especiales")
    void shouldPreserveSpecialCharactersInNames() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      ProductStock productStock =
          new ProductStock(productId, "Hamburguesa & Queso", 50, branchId, "Sucursal #1");

      // Act
      ProductStockResponse response = dtoMapper.toProductStockResponse(productStock);

      // Assert
      assertThat(response.productName()).isEqualTo("Hamburguesa & Queso");
      assertThat(response.branchName()).isEqualTo("Sucursal #1");
    }
  }

  @Nested
  @DisplayName("Casos de integración - Mapeo completo")
  class IntegrationTests {

    @Test
    @DisplayName("Debe mapear franquicia completa con jerarquía de sucursales")
    void shouldMapCompleteFranchiseHierarchy() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      Product product =
          Product.builder().id(productId).name("Hamburguesa").stock(50).branchId(branchId).build();

      branch.addProduct(product);

      Franchise franchise = Franchise.builder().id(franchiseId).name("McDonald's").build();
      franchise.addBranch(branch);

      // Act
      FranchiseResponse response = dtoMapper.toFranchiseResponse(franchise);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.branches()).hasSize(1);
      assertThat(response.branches().get(0).products()).isEmpty(); // Sin productos en listado
    }

    @Test
    @DisplayName("Debe manejar correctamente objetos vacíos")
    void shouldHandleEmptyObjects() {
      // Arrange
      Franchise emptyFranchise =
          Franchise.builder().id(UUID.randomUUID()).name("Empty Franchise").build();

      Branch emptyBranch =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Empty Branch")
              .franchiseId(UUID.randomUUID())
              .build();

      // Act
      FranchiseResponse franchiseResponse = dtoMapper.toFranchiseResponse(emptyFranchise);
      BranchResponse branchResponse = dtoMapper.toBranchResponse(emptyBranch);

      // Assert
      assertThat(franchiseResponse.branches()).isNull();
      assertThat(branchResponse.products()).isEmpty();
    }
  }
}
