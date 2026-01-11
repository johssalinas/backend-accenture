package com.accenture.franchise.infrastructure.adapter.output.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.ProductEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.repository.FranchiseJpaRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link BranchPersistenceMapper}.
 *
 * <p>Siguiendo mejores prácticas:
 *
 * <ul>
 *   <li>Uso de Mockito para mocks de repositorios y mappers
 *   <li>Patrón AAA (Arrange-Act-Assert)
 *   <li>Tests independientes y aislados
 *   <li>Verificación de mapeo completo de campos
 *   <li>Tests organizados con @Nested para agrupar casos relacionados
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BranchPersistenceMapper - Pruebas Unitarias")
class BranchPersistenceMapperTest {

  @Mock private ProductPersistenceMapper productMapper;
  @Mock private FranchiseJpaRepository franchiseJpaRepository;

  @InjectMocks private BranchPersistenceMapper branchPersistenceMapper;

  @Nested
  @DisplayName("toDomain - Mapeo de BranchEntity a Branch")
  class ToDomainTests {

    @Test
    @DisplayName("Debe mapear correctamente una entidad de sucursal a dominio")
    void shouldMapBranchEntityToDomain() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity =
          BranchEntity.builder().name("Sucursal Centro").franchise(franchiseEntity).build();
      branchEntity.setId(branchId);

      // Act
      Branch branch = branchPersistenceMapper.toDomain(branchEntity);

      // Assert
      assertThat(branch).isNotNull();
      assertThat(branch.getId()).isEqualTo(branchId);
      assertThat(branch.getName()).isEqualTo("Sucursal Centro");
      assertThat(branch.getFranchiseId()).isEqualTo(franchiseId);
    }

    @Test
    @DisplayName("Debe retornar null cuando la entidad es null")
    void shouldReturnNullWhenEntityIsNull() {
      // Act
      Branch branch = branchPersistenceMapper.toDomain(null);

      // Assert
      assertThat(branch).isNull();
    }

    @Test
    @DisplayName("Debe mapear sucursal con productos")
    void shouldMapBranchWithProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity =
          BranchEntity.builder().name("Sucursal Norte").franchise(franchiseEntity).build();
      branchEntity.setId(branchId);

      ProductEntity productEntity = ProductEntity.builder().name("Hamburguesa").stock(50).build();
      productEntity.setId(productId);
      branchEntity.addProduct(productEntity);

      Product productDomain =
          Product.builder().id(productId).name("Hamburguesa").stock(50).branchId(branchId).build();

      given(productMapper.toDomain(any(ProductEntity.class))).willReturn(productDomain);

      // Act
      Branch branch = branchPersistenceMapper.toDomain(branchEntity);

      // Assert
      assertThat(branch).isNotNull();
      assertThat(branch.getProducts()).hasSize(1);
      assertThat(branch.getProducts().get(0).getName()).isEqualTo("Hamburguesa");

      verify(productMapper).toDomain(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Debe mapear sucursal sin productos")
    void shouldMapBranchWithoutProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity =
          BranchEntity.builder().name("Sucursal Sur").franchise(franchiseEntity).build();
      branchEntity.setId(branchId);

      // Act
      Branch branch = branchPersistenceMapper.toDomain(branchEntity);

      // Assert
      assertThat(branch).isNotNull();
      assertThat(branch.getProducts()).isEmpty();
    }

    @Test
    @DisplayName("Debe mapear sucursal sin franquicia asignada")
    void shouldMapBranchWithoutFranchise() {
      // Arrange
      UUID branchId = UUID.randomUUID();

      BranchEntity branchEntity = BranchEntity.builder().name("Sucursal Sin Franquicia").build();
      branchEntity.setId(branchId);

      // Act
      Branch branch = branchPersistenceMapper.toDomain(branchEntity);

      // Assert
      assertThat(branch).isNotNull();
      assertThat(branch.getFranchiseId()).isNull();
    }

    @Test
    @DisplayName("Debe mapear múltiples productos correctamente")
    void shouldMapMultipleProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity =
          BranchEntity.builder().name("Sucursal Este").franchise(franchiseEntity).build();
      branchEntity.setId(branchId);

      ProductEntity product1 = ProductEntity.builder().name("Hamburguesa").stock(50).build();
      product1.setId(UUID.randomUUID());

      ProductEntity product2 = ProductEntity.builder().name("Papas Fritas").stock(100).build();
      product2.setId(UUID.randomUUID());

      branchEntity.addProduct(product1);
      branchEntity.addProduct(product2);

      Product productDomain1 =
          Product.builder()
              .id(product1.getId())
              .name("Hamburguesa")
              .stock(50)
              .branchId(branchId)
              .build();

      Product productDomain2 =
          Product.builder()
              .id(product2.getId())
              .name("Papas Fritas")
              .stock(100)
              .branchId(branchId)
              .build();

      given(productMapper.toDomain(product1)).willReturn(productDomain1);
      given(productMapper.toDomain(product2)).willReturn(productDomain2);

      // Act
      Branch branch = branchPersistenceMapper.toDomain(branchEntity);

      // Assert
      assertThat(branch.getProducts()).hasSize(2);
      verify(productMapper, times(2)).toDomain(any(ProductEntity.class));
    }
  }

  @Nested
  @DisplayName("toEntity - Mapeo de Branch a BranchEntity")
  class ToEntityTests {

    @Test
    @DisplayName("Debe mapear correctamente una sucursal de dominio a entidad")
    void shouldMapBranchDomainToEntity() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("McDonald's").build();
      franchiseEntity.setId(franchiseId);

      given(franchiseJpaRepository.getReferenceById(franchiseId)).willReturn(franchiseEntity);

      // Act
      BranchEntity branchEntity = branchPersistenceMapper.toEntity(branch);

      // Assert
      assertThat(branchEntity).isNotNull();
      assertThat(branchEntity.getId()).isEqualTo(branchId);
      assertThat(branchEntity.getName()).isEqualTo("Sucursal Centro");
      assertThat(branchEntity.getFranchise()).isEqualTo(franchiseEntity);

      verify(franchiseJpaRepository).getReferenceById(franchiseId);
    }

    @Test
    @DisplayName("Debe retornar null cuando el dominio es null")
    void shouldReturnNullWhenDomainIsNull() {
      // Act
      BranchEntity branchEntity = branchPersistenceMapper.toEntity(null);

      // Assert
      assertThat(branchEntity).isNull();
    }

    @Test
    @DisplayName("Debe mapear sucursal sin ID (nueva)")
    void shouldMapBranchWithoutId() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Branch branch = Branch.builder().name("Sucursal Nueva").franchiseId(franchiseId).build();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      given(franchiseJpaRepository.getReferenceById(franchiseId)).willReturn(franchiseEntity);

      // Act
      BranchEntity branchEntity = branchPersistenceMapper.toEntity(branch);

      // Assert
      assertThat(branchEntity).isNotNull();
      assertThat(branchEntity.getId()).isNull();
      assertThat(branchEntity.getName()).isEqualTo("Sucursal Nueva");
    }

    @Test
    @DisplayName("Debe mapear sucursal sin franchiseId")
    void shouldMapBranchWithoutFranchiseId() {
      // Arrange
      UUID branchId = UUID.randomUUID();

      Branch branch = Branch.builder().id(branchId).name("Sucursal Sin Franquicia").build();

      // Act
      BranchEntity branchEntity = branchPersistenceMapper.toEntity(branch);

      // Assert
      assertThat(branchEntity).isNotNull();
      assertThat(branchEntity.getFranchise()).isNull();
    }

    @Test
    @DisplayName("Debe mapear sucursal con productos")
    void shouldMapBranchWithProducts() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      Product product =
          Product.builder().id(productId).name("Hamburguesa").stock(50).branchId(branchId).build();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Norte").franchiseId(franchiseId).build();
      branch.addProduct(product);

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      ProductEntity productEntity = ProductEntity.builder().name("Hamburguesa").stock(50).build();
      productEntity.setId(productId);

      given(franchiseJpaRepository.getReferenceById(franchiseId)).willReturn(franchiseEntity);
      given(productMapper.toEntity(any(Product.class))).willReturn(productEntity);

      // Act
      BranchEntity branchEntity = branchPersistenceMapper.toEntity(branch);

      // Assert
      assertThat(branchEntity).isNotNull();
      assertThat(branchEntity.getProducts()).hasSize(1);

      verify(productMapper).toEntity(any(Product.class));
    }

    @Test
    @DisplayName("Debe establecer la relación con FranchiseEntity correctamente")
    void shouldSetFranchiseEntityRelationshipCorrectly() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Sur").franchiseId(franchiseId).build();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("Burger King").build();
      franchiseEntity.setId(franchiseId);

      given(franchiseJpaRepository.getReferenceById(franchiseId)).willReturn(franchiseEntity);

      // Act
      BranchEntity branchEntity = branchPersistenceMapper.toEntity(branch);

      // Assert
      assertThat(branchEntity.getFranchise()).isNotNull();
      assertThat(branchEntity.getFranchise().getId()).isEqualTo(franchiseId);
      assertThat(branchEntity.getFranchise().getName()).isEqualTo("Burger King");
    }

    @Test
    @DisplayName("Debe usar getReferenceById del repositorio para la relación")
    void shouldUseGetReferenceByIdForRelationship() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Test").franchiseId(franchiseId).build();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      given(franchiseJpaRepository.getReferenceById(franchiseId)).willReturn(franchiseEntity);

      // Act
      branchPersistenceMapper.toEntity(branch);

      // Assert
      verify(franchiseJpaRepository).getReferenceById(franchiseId);
    }
  }

  @Nested
  @DisplayName("Casos de integración - Mapeo bidireccional")
  class IntegrationTests {

    @Test
    @DisplayName("Debe mantener consistencia en mapeo bidireccional")
    void shouldMaintainConsistencyInBidirectionalMapping() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("KFC").build();
      franchiseEntity.setId(franchiseId);

      BranchEntity originalEntity =
          BranchEntity.builder().name("Sucursal Centro").franchise(franchiseEntity).build();
      originalEntity.setId(branchId);

      given(franchiseJpaRepository.getReferenceById(franchiseId)).willReturn(franchiseEntity);

      // Act - Mapeo de ida y vuelta
      Branch domain = branchPersistenceMapper.toDomain(originalEntity);
      BranchEntity mappedEntity = branchPersistenceMapper.toEntity(domain);

      // Assert - Verificar que los datos se mantienen
      assertThat(mappedEntity.getId()).isEqualTo(branchId);
      assertThat(mappedEntity.getName()).isEqualTo("Sucursal Centro");
      assertThat(mappedEntity.getFranchise().getId()).isEqualTo(franchiseId);
    }

    @Test
    @DisplayName("Debe manejar correctamente sucursales con nombres largos")
    void shouldHandleBranchesWithLongNames() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      String longName =
          "Sucursal Centro Comercial Plaza Mayor Internacional - Nivel 3 Zona Gastronómica";

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity =
          BranchEntity.builder().name(longName).franchise(franchiseEntity).build();
      branchEntity.setId(branchId);

      // Act
      Branch branch = branchPersistenceMapper.toDomain(branchEntity);

      // Assert
      assertThat(branch.getName()).isEqualTo(longName);
      assertThat(branch.getName().length()).isGreaterThan(50);
    }

    @Test
    @DisplayName("Debe preservar todos los campos en el mapeo completo")
    void shouldPreserveAllFieldsInCompleteMapping() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      Branch originalBranch =
          Branch.builder().id(branchId).name("Sucursal Este").franchiseId(franchiseId).build();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      given(franchiseJpaRepository.getReferenceById(franchiseId)).willReturn(franchiseEntity);

      // Act
      BranchEntity entity = branchPersistenceMapper.toEntity(originalBranch);
      Branch mappedBranch = branchPersistenceMapper.toDomain(entity);

      // Assert - Todos los campos deben mantenerse
      assertThat(mappedBranch.getId()).isEqualTo(originalBranch.getId());
      assertThat(mappedBranch.getName()).isEqualTo(originalBranch.getName());
      assertThat(mappedBranch.getFranchiseId()).isEqualTo(originalBranch.getFranchiseId());
    }

    @Test
    @DisplayName("Debe preservar nombres con caracteres especiales")
    void shouldPreserveNamesWithSpecialCharacters() {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity =
          BranchEntity.builder()
              .name("Sucursal #1 - Centro & Mall")
              .franchise(franchiseEntity)
              .build();
      branchEntity.setId(branchId);

      // Act
      Branch branch = branchPersistenceMapper.toDomain(branchEntity);

      // Assert
      assertThat(branch.getName()).isEqualTo("Sucursal #1 - Centro & Mall");
    }
  }
}
