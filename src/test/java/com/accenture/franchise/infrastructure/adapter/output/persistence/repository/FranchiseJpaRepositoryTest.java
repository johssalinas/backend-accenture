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

/** Tests de integración para FranchiseJpaRepository. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FranchiseJpaRepository - Pruebas de Integración")
class FranchiseJpaRepositoryTest {

  @Autowired private FranchiseJpaRepository franchiseRepository;
  @Autowired private BranchJpaRepository branchRepository;
  @Autowired private ProductJpaRepository productRepository;

  @AfterEach
  void cleanup() {
    productRepository.deleteAll();
    branchRepository.deleteAll();
    franchiseRepository.deleteAll();
  }

  @Nested
  @DisplayName("save - Guardar Franquicia")
  class SaveTests {

    @Test
    @DisplayName("Debe guardar una nueva franquicia")
    void shouldSaveNewFranchise() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("McDonald's").build();

      // Act
      FranchiseEntity savedFranchise = franchiseRepository.save(franchise);

      // Assert
      assertThat(savedFranchise.getId()).isNotNull();
      assertThat(savedFranchise.getName()).isEqualTo("McDonald's");
    }

    @Test
    @DisplayName("Debe actualizar una franquicia existente")
    void shouldUpdateExistingFranchise() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("KFC").build();
      FranchiseEntity savedFranchise = franchiseRepository.save(franchise);
      UUID franchiseId = savedFranchise.getId();

      // Act
      savedFranchise.setName("KFC Updated");
      FranchiseEntity updatedFranchise = franchiseRepository.save(savedFranchise);

      // Assert
      FranchiseEntity foundFranchise = franchiseRepository.findById(franchiseId).get();
      assertThat(foundFranchise.getName()).isEqualTo("KFC Updated");
    }

    @Test
    @DisplayName("Debe generar ID automáticamente")
    void shouldGenerateIdAutomatically() {
      // Arrange
      FranchiseEntity franchise1 = FranchiseEntity.builder().name("Franchise 1").build();
      FranchiseEntity franchise2 = FranchiseEntity.builder().name("Franchise 2").build();

      // Act
      FranchiseEntity saved1 = franchiseRepository.save(franchise1);
      FranchiseEntity saved2 = franchiseRepository.save(franchise2);

      // Assert
      assertThat(saved1.getId()).isNotNull();
      assertThat(saved2.getId()).isNotNull();
      assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
    }
  }

  @Nested
  @DisplayName("findById - Buscar por ID")
  class FindByIdTests {

    @Test
    @DisplayName("Debe encontrar franquicia por ID")
    void shouldFindFranchiseById() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Burger King").build();
      FranchiseEntity savedFranchise = franchiseRepository.save(franchise);
      UUID franchiseId = savedFranchise.getId();

      // Act
      Optional<FranchiseEntity> foundFranchise = franchiseRepository.findById(franchiseId);

      // Assert
      assertThat(foundFranchise).isPresent();
      assertThat(foundFranchise.get().getName()).isEqualTo("Burger King");
    }

    @Test
    @DisplayName("Debe retornar vacío si no existe la franquicia")
    void shouldReturnEmptyIfFranchiseNotExists() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      // Act
      Optional<FranchiseEntity> foundFranchise = franchiseRepository.findById(nonExistentId);

      // Assert
      assertThat(foundFranchise).isEmpty();
    }
  }

  @Nested
  @DisplayName("findAll - Buscar todas")
  class FindAllTests {

    @Test
    @DisplayName("Debe retornar todas las franquicias")
    void shouldReturnAllFranchises() {
      // Arrange
      FranchiseEntity franchise1 = FranchiseEntity.builder().name("Franchise A").build();
      FranchiseEntity franchise2 = FranchiseEntity.builder().name("Franchise B").build();
      FranchiseEntity franchise3 = FranchiseEntity.builder().name("Franchise C").build();

      franchiseRepository.save(franchise1);
      franchiseRepository.save(franchise2);
      franchiseRepository.save(franchise3);

      // Act
      List<FranchiseEntity> franchises = franchiseRepository.findAll();

      // Assert
      assertThat(franchises).hasSize(3);
      assertThat(franchises)
          .extracting(FranchiseEntity::getName)
          .containsExactlyInAnyOrder("Franchise A", "Franchise B", "Franchise C");
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay franquicias")
    void shouldReturnEmptyListIfNoFranchises() {
      // Act
      List<FranchiseEntity> franchises = franchiseRepository.findAll();

      // Assert
      assertThat(franchises).isEmpty();
    }
  }

  @Nested
  @DisplayName("existsByName - Verificar existencia por nombre")
  class ExistsByNameTests {

    @Test
    @DisplayName("Debe retornar true si existe franquicia con el nombre")
    void shouldReturnTrueIfFranchiseExists() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Subway").build();
      franchiseRepository.save(franchise);

      // Act
      boolean exists = franchiseRepository.existsByName("Subway");

      // Assert
      assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false si no existe franquicia con el nombre")
    void shouldReturnFalseIfFranchiseNotExists() {
      // Act
      boolean exists = franchiseRepository.existsByName("Non Existent Franchise");

      // Assert
      assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe ser case-sensitive en la búsqueda")
    void shouldBeCaseSensitive() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Pizza Hut").build();
      franchiseRepository.save(franchise);

      // Act
      boolean existsExact = franchiseRepository.existsByName("Pizza Hut");
      boolean existsLower = franchiseRepository.existsByName("pizza hut");
      boolean existsUpper = franchiseRepository.existsByName("PIZZA HUT");

      // Assert
      assertThat(existsExact).isTrue();
      assertThat(existsLower).isFalse();
      assertThat(existsUpper).isFalse();
    }
  }

  @Nested
  @DisplayName("findByIdWithBranchesAndProducts - Buscar con relaciones")
  class FindByIdWithBranchesAndProductsTests {

    @Test
    @DisplayName("Debe cargar franquicia con sucursales y productos")
    void shouldLoadFranchiseWithBranchesAndProducts() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("McDonald's").build();
      franchise = franchiseRepository.save(franchise);

      BranchEntity branch1 =
          BranchEntity.builder().name("Sucursal Centro").franchise(franchise).build();
      BranchEntity branch2 =
          BranchEntity.builder().name("Sucursal Norte").franchise(franchise).build();
      branch1 = branchRepository.save(branch1);
      branch2 = branchRepository.save(branch2);

      ProductEntity product1 =
          ProductEntity.builder().name("Big Mac").stock(50).branch(branch1).build();
      ProductEntity product2 =
          ProductEntity.builder().name("Papas").stock(100).branch(branch1).build();
      ProductEntity product3 =
          ProductEntity.builder().name("Nuggets").stock(75).branch(branch2).build();
      productRepository.save(product1);
      productRepository.save(product2);
      productRepository.save(product3);

      UUID franchiseId = franchise.getId();

      // Act
      Optional<FranchiseEntity> foundFranchise =
          franchiseRepository.findByIdWithBranchesAndProducts(franchiseId);

      // Assert
      assertThat(foundFranchise).isPresent();
      assertThat(foundFranchise.get().getName()).isEqualTo("McDonald's");

      // Verificar que las sucursales existen en la base de datos
      List<BranchEntity> branches = branchRepository.findByFranchiseId(franchiseId);
      assertThat(branches).hasSize(2);
      assertThat(branches)
          .extracting(BranchEntity::getName)
          .containsExactlyInAnyOrder("Sucursal Centro", "Sucursal Norte");
    }

    @Test
    @DisplayName("Debe cargar franquicia sin sucursales")
    void shouldLoadFranchiseWithoutBranches() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("KFC").build();
      franchise = franchiseRepository.save(franchise);
      UUID franchiseId = franchise.getId();

      // Act
      Optional<FranchiseEntity> foundFranchise =
          franchiseRepository.findByIdWithBranchesAndProducts(franchiseId);

      // Assert
      assertThat(foundFranchise).isPresent();
      assertThat(foundFranchise.get().getName()).isEqualTo("KFC");
      assertThat(foundFranchise.get().getBranches()).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar vacío si no existe la franquicia")
    void shouldReturnEmptyIfFranchiseNotExists() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      // Act
      Optional<FranchiseEntity> foundFranchise =
          franchiseRepository.findByIdWithBranchesAndProducts(nonExistentId);

      // Assert
      assertThat(foundFranchise).isEmpty();
    }
  }

  @Nested
  @DisplayName("delete - Eliminar Franquicia")
  class DeleteTests {

    @Test
    @DisplayName("Debe eliminar franquicia por ID")
    void shouldDeleteFranchiseById() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Starbucks").build();
      FranchiseEntity savedFranchise = franchiseRepository.save(franchise);
      UUID franchiseId = savedFranchise.getId();

      // Act
      franchiseRepository.deleteById(franchiseId);

      // Assert
      Optional<FranchiseEntity> foundFranchise = franchiseRepository.findById(franchiseId);
      assertThat(foundFranchise).isEmpty();
    }

    @Test
    @DisplayName("Debe eliminar franquicia con entidad")
    void shouldDeleteFranchiseWithEntity() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Dunkin").build();
      FranchiseEntity savedFranchise = franchiseRepository.save(franchise);
      UUID franchiseId = savedFranchise.getId();

      // Act
      franchiseRepository.delete(savedFranchise);

      // Assert
      Optional<FranchiseEntity> foundFranchise = franchiseRepository.findById(franchiseId);
      assertThat(foundFranchise).isEmpty();
    }

    @Test
    @DisplayName("No debe lanzar error al eliminar ID inexistente")
    void shouldNotThrowErrorWhenDeletingNonExistentId() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      // Act & Assert
      franchiseRepository.deleteById(nonExistentId);
      // No debe lanzar excepción
    }
  }

  @Nested
  @DisplayName("count - Contar Franquicias")
  class CountTests {

    @Test
    @DisplayName("Debe contar correctamente las franquicias")
    void shouldCountFranchisesCorrectly() {
      // Arrange
      for (int i = 1; i <= 7; i++) {
        FranchiseEntity franchise = FranchiseEntity.builder().name("Franchise " + i).build();
        franchiseRepository.save(franchise);
      }

      // Act
      long count = franchiseRepository.count();

      // Assert
      assertThat(count).isEqualTo(7);
    }

    @Test
    @DisplayName("Debe retornar 0 si no hay franquicias")
    void shouldReturnZeroIfNoFranchises() {
      // Act
      long count = franchiseRepository.count();

      // Assert
      assertThat(count).isZero();
    }
  }

  @Nested
  @DisplayName("existsById - Verificar existencia por ID")
  class ExistsByIdTests {

    @Test
    @DisplayName("Debe retornar true si existe franquicia con el ID")
    void shouldReturnTrueIfFranchiseExists() {
      // Arrange
      FranchiseEntity franchise = FranchiseEntity.builder().name("Wendy's").build();
      FranchiseEntity savedFranchise = franchiseRepository.save(franchise);
      UUID franchiseId = savedFranchise.getId();

      // Act
      boolean exists = franchiseRepository.existsById(franchiseId);

      // Assert
      assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false si no existe franquicia con el ID")
    void shouldReturnFalseIfFranchiseNotExists() {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      // Act
      boolean exists = franchiseRepository.existsById(nonExistentId);

      // Assert
      assertThat(exists).isFalse();
    }
  }
}
