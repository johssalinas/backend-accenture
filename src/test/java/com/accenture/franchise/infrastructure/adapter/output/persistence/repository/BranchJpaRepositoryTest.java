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

/** Tests de integración para BranchJpaRepository. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("BranchJpaRepository - Pruebas de Integración")
class BranchJpaRepositoryTest {

  @Autowired private BranchJpaRepository branchRepository;
  @Autowired private FranchiseJpaRepository franchiseRepository;
  @Autowired private ProductJpaRepository productRepository;

  @AfterEach
  void cleanup() {
    productRepository.deleteAll();
    branchRepository.deleteAll();
    franchiseRepository.deleteAll();
  }

  @Nested
  @DisplayName("save - Guardar Sucursal")
  class SaveTests {

    @Test
    @DisplayName("Debe guardar una nueva sucursal")
    void shouldSaveNewBranch() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("McDonald's").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Centro").franchise(franchise).build();

      // Act
      BranchEntity savedBranch = branchRepository.save(branch);

      // Assert
      assertThat(savedBranch.getId()).isNotNull();
      assertThat(savedBranch.getName()).isEqualTo("Sucursal Centro");
      assertThat(savedBranch.getFranchise().getId()).isEqualTo(franchise.getId());
    }

    @Test
    @DisplayName("Debe actualizar una sucursal existente")
    void shouldUpdateExistingBranch() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("KFC").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Norte").franchise(franchise).build();
      branch = branchRepository.save(branch);
      UUID branchId = branch.getId();

      // Act
      branch.setName("Sucursal Norte Actualizada");
      BranchEntity updatedBranch = branchRepository.save(branch);

      // Assert
      BranchEntity foundBranch = branchRepository.findById(branchId).get();
      assertThat(foundBranch.getName()).isEqualTo("Sucursal Norte Actualizada");
    }
  }

  @Nested
  @DisplayName("findByFranchiseId - Buscar por franquicia")
  class FindByFranchiseIdTests {

    @Test
    @DisplayName("Debe retornar todas las sucursales de una franquicia")
    void shouldReturnAllBranchesOfFranchise() {
      // Arrange
      FranchiseEntity franchise1 = FranchiseEntity.builder().name("McDonald's").build();
      FranchiseEntity franchise2 = FranchiseEntity.builder().name("KFC").build();
      franchise1 = franchiseRepository.save(franchise1);
      franchise2 = franchiseRepository.save(franchise2);

      branchRepository.save(
          BranchEntity.builder().name("McD Centro").franchise(franchise1).build());
      branchRepository.save(BranchEntity.builder().name("McD Norte").franchise(franchise1).build());
      branchRepository.save(BranchEntity.builder().name("McD Sur").franchise(franchise1).build());
      branchRepository.save(
          BranchEntity.builder().name("KFC Centro").franchise(franchise2).build());

      // Act
      List<BranchEntity> branches = branchRepository.findByFranchiseId(franchise1.getId());

      // Assert
      assertThat(branches).hasSize(3);
      assertThat(branches)
          .extracting(BranchEntity::getName)
          .containsExactlyInAnyOrder("McD Centro", "McD Norte", "McD Sur");
    }

    @Test
    @DisplayName("Debe retornar lista vacía si la franquicia no tiene sucursales")
    void shouldReturnEmptyListIfFranchiseHasNoBranches() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Dominos").build();
      franchise = franchiseRepository.save(franchise);

      // Act
      List<BranchEntity> branches = branchRepository.findByFranchiseId(franchise.getId());

      // Assert
      assertThat(branches).isEmpty();
    }
  }

  @Nested
  @DisplayName("existsByNameAndFranchiseId - Verificar existencia")
  class ExistsByNameAndFranchiseIdTests {

    @Test
    @DisplayName("Debe retornar true si existe sucursal en la franquicia")
    void shouldReturnTrueIfBranchExists() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Starbucks").build();
      franchise = franchiseRepository.save(franchise);

      branchRepository.save(
          BranchEntity.builder().name("Sucursal Polanco").franchise(franchise).build());

      // Act
      boolean exists =
          branchRepository.existsByNameAndFranchiseId("Sucursal Polanco", franchise.getId());

      // Assert
      assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe permitir mismo nombre en franquicias diferentes")
    void shouldAllowSameNameInDifferentFranchises() {
      // Arrange
      FranchiseEntity franchise1 = FranchiseEntity.builder().name("Franchise 1").build();
      FranchiseEntity franchise2 = FranchiseEntity.builder().name("Franchise 2").build();
      franchise1 = franchiseRepository.save(franchise1);
      franchise2 = franchiseRepository.save(franchise2);

      branchRepository.save(
          BranchEntity.builder().name("Sucursal Centro").franchise(franchise1).build());
      branchRepository.save(
          BranchEntity.builder().name("Sucursal Centro").franchise(franchise2).build());

      // Act
      boolean existsInFranchise1 =
          branchRepository.existsByNameAndFranchiseId("Sucursal Centro", franchise1.getId());
      boolean existsInFranchise2 =
          branchRepository.existsByNameAndFranchiseId("Sucursal Centro", franchise2.getId());

      // Assert
      assertThat(existsInFranchise1).isTrue();
      assertThat(existsInFranchise2).isTrue();
    }
  }

  @Nested
  @DisplayName("findByIdWithProducts - Buscar con productos")
  class FindByIdWithProductsTests {

    @Test
    @DisplayName("Debe cargar sucursal con productos")
    void shouldLoadBranchWithProducts() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("McDonald's").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Centro").franchise(franchise).build();
      branch = branchRepository.save(branch);

      ProductEntity product1 =
          ProductEntity.builder().name("Big Mac").stock(50).branch(branch).build();
      ProductEntity product2 =
          ProductEntity.builder().name("Papas").stock(100).branch(branch).build();
      ProductEntity product3 =
          ProductEntity.builder().name("Nuggets").stock(75).branch(branch).build();
      productRepository.save(product1);
      productRepository.save(product2);
      productRepository.save(product3);

      UUID branchId = branch.getId();

      // Act
      Optional<BranchEntity> foundBranch = branchRepository.findByIdWithProducts(branchId);

      // Assert
      assertThat(foundBranch).isPresent();
      assertThat(foundBranch.get().getName()).isEqualTo("Sucursal Centro");
      assertThat(foundBranch.get().getFranchise()).isNotNull();
      assertThat(foundBranch.get().getFranchise().getName()).isEqualTo("McDonald's");

      // Verificar que los productos existen en la base de datos
      List<ProductEntity> products = productRepository.findByBranchId(branchId);
      assertThat(products).hasSize(3);
    }

    @Test
    @DisplayName("Debe cargar sucursal sin productos")
    void shouldLoadBranchWithoutProducts() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("KFC").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Norte").franchise(franchise).build();
      branch = branchRepository.save(branch);
      UUID branchId = branch.getId();

      // Act
      Optional<BranchEntity> foundBranch = branchRepository.findByIdWithProducts(branchId);

      // Assert
      assertThat(foundBranch).isPresent();
      assertThat(foundBranch.get().getName()).isEqualTo("Sucursal Norte");
      assertThat(foundBranch.get().getProducts()).isEmpty();
    }
  }

  @Nested
  @DisplayName("delete - Eliminar Sucursal")
  class DeleteTests {

    @Test
    @DisplayName("Debe eliminar sucursal por ID")
    void shouldDeleteBranchById() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Subway").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch =
          BranchEntity.builder().name("Sucursal Juarez").franchise(franchise).build();
      branch = branchRepository.save(branch);
      UUID branchId = branch.getId();

      // Act
      branchRepository.deleteById(branchId);

      // Assert
      Optional<BranchEntity> foundBranch = branchRepository.findById(branchId);
      assertThat(foundBranch).isEmpty();
    }
  }

  @Nested
  @DisplayName("count - Contar Sucursales")
  class CountTests {

    @Test
    @DisplayName("Debe contar correctamente las sucursales")
    void shouldCountBranchesCorrectly() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Starbucks").build();
      franchise = franchiseRepository.save(franchise);

      for (int i = 1; i <= 5; i++) {
        branchRepository.save(
            BranchEntity.builder().name("Branch " + i).franchise(franchise).build());
      }

      // Act
      long count = branchRepository.count();

      // Assert
      assertThat(count).isEqualTo(5);
    }
  }
}
