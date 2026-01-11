package com.accenture.franchise.infrastructure.adapter.output.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.ProductEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/** Tests de integración para ProductJpaRepository. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductJpaRepository - Pruebas de Integración")
class ProductJpaRepositoryTest {

  @Autowired private ProductJpaRepository productRepository;
  @Autowired private BranchJpaRepository branchRepository;
  @Autowired private FranchiseJpaRepository franchiseRepository;

  @AfterEach
  void cleanup() {
    productRepository.deleteAll();
    branchRepository.deleteAll();
    franchiseRepository.deleteAll();
  }

  @Nested
  @DisplayName("save - Guardar Producto")
  class SaveTests {

    @Test
    @DisplayName("Debe guardar un nuevo producto")
    void shouldSaveNewProduct() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("McDonald's").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Centro").franchise(franchise).build();
      branch = branchRepository.save(branch);

      ProductEntity product =
          ProductEntity.builder().name("Big Mac").stock(50).branch(branch).build();

      // Act
      ProductEntity savedProduct = productRepository.save(product);

      // Assert
      assertThat(savedProduct.getId()).isNotNull();
      assertThat(savedProduct.getName()).isEqualTo("Big Mac");
      assertThat(savedProduct.getStock()).isEqualTo(50);
      assertThat(savedProduct.getBranch().getId()).isEqualTo(branch.getId());
    }

    @Test
    @DisplayName("Debe actualizar un producto existente")
    void shouldUpdateExistingProduct() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("KFC").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Norte").franchise(franchise).build();
      branch = branchRepository.save(branch);

      ProductEntity product =
          ProductEntity.builder().name("Chicken Bucket").stock(30).branch(branch).build();
      product = productRepository.save(product);
      UUID productId = product.getId();

      // Act
      product.setName("Chicken Bucket Updated");
      product.setStock(45);
      ProductEntity updatedProduct = productRepository.save(product);

      // Assert
      ProductEntity foundProduct = productRepository.findById(productId).get();
      assertThat(foundProduct.getName()).isEqualTo("Chicken Bucket Updated");
      assertThat(foundProduct.getStock()).isEqualTo(45);
    }
  }

  @Nested
  @DisplayName("findByBranchId - Buscar por sucursal")
  class FindByBranchIdTests {

    @Test
    @DisplayName("Debe retornar todos los productos de una sucursal")
    void shouldReturnAllProductsOfBranch() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("McDonald's").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch1 = BranchEntity.builder().name("Sucursal A").franchise(franchise).build();
      BranchEntity branch2 = BranchEntity.builder().name("Sucursal B").franchise(franchise).build();
      branch1 = branchRepository.save(branch1);
      branch2 = branchRepository.save(branch2);

      productRepository.save(
          ProductEntity.builder().name("Big Mac").stock(50).branch(branch1).build());
      productRepository.save(
          ProductEntity.builder().name("Papas").stock(100).branch(branch1).build());
      productRepository.save(
          ProductEntity.builder().name("Nuggets").stock(75).branch(branch1).build());
      productRepository.save(
          ProductEntity.builder().name("McFlurry").stock(30).branch(branch2).build());

      // Act
      List<ProductEntity> products = productRepository.findByBranchId(branch1.getId());

      // Assert
      assertThat(products).hasSize(3);
      assertThat(products)
          .extracting(ProductEntity::getName)
          .containsExactlyInAnyOrder("Big Mac", "Papas", "Nuggets");
    }

    @Test
    @DisplayName("Debe retornar lista vacía si la sucursal no tiene productos")
    void shouldReturnEmptyListIfBranchHasNoProducts() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("KFC").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Vacía").franchise(franchise).build();
      branch = branchRepository.save(branch);

      // Act
      List<ProductEntity> products = productRepository.findByBranchId(branch.getId());

      // Assert
      assertThat(products).isEmpty();
    }
  }

  @Nested
  @DisplayName("existsByNameAndBranchId - Verificar existencia")
  class ExistsByNameAndBranchIdTests {

    @Test
    @DisplayName("Debe retornar true si existe producto en la sucursal")
    void shouldReturnTrueIfProductExists() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Starbucks").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Polanco").franchise(franchise).build();
      branch = branchRepository.save(branch);

      productRepository.save(
          ProductEntity.builder().name("Frappuccino").stock(40).branch(branch).build());

      // Act
      boolean exists = productRepository.existsByNameAndBranchId("Frappuccino", branch.getId());

      // Assert
      assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe permitir mismo nombre en sucursales diferentes")
    void shouldAllowSameNameInDifferentBranches() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Franchise").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch1 = BranchEntity.builder().name("Branch 1").franchise(franchise).build();
      BranchEntity branch2 = BranchEntity.builder().name("Branch 2").franchise(franchise).build();
      branch1 = branchRepository.save(branch1);
      branch2 = branchRepository.save(branch2);

      productRepository.save(
          ProductEntity.builder().name("Hamburguesa").stock(30).branch(branch1).build());
      productRepository.save(
          ProductEntity.builder().name("Hamburguesa").stock(40).branch(branch2).build());

      // Act
      boolean existsInBranch1 =
          productRepository.existsByNameAndBranchId("Hamburguesa", branch1.getId());
      boolean existsInBranch2 =
          productRepository.existsByNameAndBranchId("Hamburguesa", branch2.getId());

      // Assert
      assertThat(existsInBranch1).isTrue();
      assertThat(existsInBranch2).isTrue();
    }
  }

  @Nested
  @DisplayName("findByBranchIdOrderByStockDesc - Ordenar por stock")
  class FindByBranchIdOrderByStockDescTests {

    @Test
    @DisplayName("Debe retornar productos ordenados por stock descendente")
    void shouldReturnProductsOrderedByStockDesc() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("McDonald's").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Centro").franchise(franchise).build();
      branch = branchRepository.save(branch);

      productRepository.save(
          ProductEntity.builder().name("Producto A").stock(10).branch(branch).build());
      productRepository.save(
          ProductEntity.builder().name("Producto B").stock(50).branch(branch).build());
      productRepository.save(
          ProductEntity.builder().name("Producto C").stock(30).branch(branch).build());
      productRepository.save(
          ProductEntity.builder().name("Producto D").stock(75).branch(branch).build());

      // Act
      List<ProductEntity> products =
          productRepository.findByBranchIdOrderByStockDesc(branch.getId());

      // Assert
      assertThat(products).hasSize(4);
      assertThat(products.get(0).getName()).isEqualTo("Producto D");
      assertThat(products.get(0).getStock()).isEqualTo(75);
      assertThat(products.get(1).getName()).isEqualTo("Producto B");
      assertThat(products.get(1).getStock()).isEqualTo(50);
      assertThat(products.get(2).getName()).isEqualTo("Producto C");
      assertThat(products.get(2).getStock()).isEqualTo(30);
      assertThat(products.get(3).getName()).isEqualTo("Producto A");
      assertThat(products.get(3).getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("Debe incluir productos con stock cero")
    void shouldIncludeProductsWithZeroStock() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Subway").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Sur").franchise(franchise).build();
      branch = branchRepository.save(branch);

      productRepository.save(
          ProductEntity.builder().name("Producto High").stock(100).branch(branch).build());
      productRepository.save(
          ProductEntity.builder().name("Producto Zero").stock(0).branch(branch).build());
      productRepository.save(
          ProductEntity.builder().name("Producto Mid").stock(50).branch(branch).build());

      // Act
      List<ProductEntity> products =
          productRepository.findByBranchIdOrderByStockDesc(branch.getId());

      // Assert
      assertThat(products).hasSize(3);
      assertThat(products.get(2).getName()).isEqualTo("Producto Zero");
      assertThat(products.get(2).getStock()).isZero();
    }
  }

  @Nested
  @DisplayName("delete - Eliminar Producto")
  class DeleteTests {

    @Test
    @DisplayName("Debe eliminar producto por ID")
    void shouldDeleteProductById() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Pizza Hut").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Condesa").franchise(franchise).build();
      branch = branchRepository.save(branch);

      ProductEntity product =
          ProductEntity.builder().name("Pizza Hawaiana").stock(15).branch(branch).build();
      product = productRepository.save(product);
      UUID productId = product.getId();

      // Act
      productRepository.deleteById(productId);

      // Assert
      Optional<ProductEntity> foundProduct = productRepository.findById(productId);
      assertThat(foundProduct).isEmpty();
    }
  }

  @Nested
  @DisplayName("count - Contar Productos")
  class CountTests {

    @Test
    @DisplayName("Debe contar correctamente los productos")
    void shouldCountProductsCorrectly() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Burger King").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Juarez").franchise(franchise).build();
      branch = branchRepository.save(branch);

      for (int i = 1; i <= 8; i++) {
        productRepository.save(
            ProductEntity.builder().name("Product " + i).stock(i * 5).branch(branch).build());
      }

      // Act
      long count = productRepository.count();

      // Assert
      assertThat(count).isEqualTo(8);
    }
  }
}
