package com.accenture.franchise.infrastructure.adapter.output.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.domain.model.Product;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.ProductEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.repository.BranchJpaRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link ProductPersistenceMapper}.
 *
 * <p>Siguiendo mejores prácticas:
 *
 * <ul>
 *   <li>Uso de Mockito para mocks de repositorios JPA
 *   <li>Patrón AAA (Arrange-Act-Assert)
 *   <li>Tests independientes y aislados
 *   <li>Verificación de mapeo completo de campos
 *   <li>Tests organizados con @Nested para agrupar casos relacionados
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductPersistenceMapper - Pruebas Unitarias")
class ProductPersistenceMapperTest {

  @Mock private BranchJpaRepository branchJpaRepository;

  @InjectMocks private ProductPersistenceMapper productPersistenceMapper;

  @Nested
  @DisplayName("toDomain - Mapeo de ProductEntity a Product")
  class ToDomainTests {

    @Test
    @DisplayName("Debe mapear correctamente una entidad de producto a dominio")
    void shouldMapProductEntityToDomain() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      ProductEntity productEntity =
          ProductEntity.builder().name("Hamburguesa").stock(50).branch(branchEntity).build();
      productEntity.setId(productId);

      // Act
      Product product = productPersistenceMapper.toDomain(productEntity);

      // Assert
      assertThat(product).isNotNull();
      assertThat(product.getId()).isEqualTo(productId);
      assertThat(product.getName()).isEqualTo("Hamburguesa");
      assertThat(product.getStock()).isEqualTo(50);
      assertThat(product.getBranchId()).isEqualTo(branchId);
    }

    @Test
    @DisplayName("Debe retornar null cuando la entidad es null")
    void shouldReturnNullWhenEntityIsNull() {
      // Act
      Product product = productPersistenceMapper.toDomain(null);

      // Assert
      assertThat(product).isNull();
    }

    @Test
    @DisplayName("Debe mapear producto con stock cero")
    void shouldMapProductWithZeroStock() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      ProductEntity productEntity =
          ProductEntity.builder().name("Producto Agotado").stock(0).branch(branchEntity).build();
      productEntity.setId(productId);

      // Act
      Product product = productPersistenceMapper.toDomain(productEntity);

      // Assert
      assertThat(product.getStock()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe mapear producto sin sucursal asignada")
    void shouldMapProductWithoutBranch() {
      // Arrange
      UUID productId = UUID.randomUUID();

      ProductEntity productEntity =
          ProductEntity.builder().name("Producto Sin Sucursal").stock(10).build();
      productEntity.setId(productId);

      // Act
      Product product = productPersistenceMapper.toDomain(productEntity);

      // Assert
      assertThat(product).isNotNull();
      assertThat(product.getBranchId()).isNull();
    }

    @Test
    @DisplayName("Debe mapear producto con stock grande")
    void shouldMapProductWithLargeStock() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      ProductEntity productEntity =
          ProductEntity.builder()
              .name("Producto Popular")
              .stock(999999)
              .branch(branchEntity)
              .build();
      productEntity.setId(productId);

      // Act
      Product product = productPersistenceMapper.toDomain(productEntity);

      // Assert
      assertThat(product.getStock()).isEqualTo(999999);
    }

    @Test
    @DisplayName("Debe preservar nombres con caracteres especiales")
    void shouldPreserveNamesWithSpecialCharacters() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      ProductEntity productEntity =
          ProductEntity.builder()
              .name("Hamburguesa & Queso")
              .stock(25)
              .branch(branchEntity)
              .build();
      productEntity.setId(productId);

      // Act
      Product product = productPersistenceMapper.toDomain(productEntity);

      // Assert
      assertThat(product.getName()).isEqualTo("Hamburguesa & Queso");
    }
  }

  @Nested
  @DisplayName("toEntity - Mapeo de Product a ProductEntity")
  class ToEntityTests {

    @Test
    @DisplayName("Debe mapear correctamente un producto de dominio a entidad")
    void shouldMapProductDomainToEntity() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder().id(productId).name("Hamburguesa").stock(50).branchId(branchId).build();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      given(branchJpaRepository.getReferenceById(branchId)).willReturn(branchEntity);

      // Act
      ProductEntity productEntity = productPersistenceMapper.toEntity(product);

      // Assert
      assertThat(productEntity).isNotNull();
      assertThat(productEntity.getId()).isEqualTo(productId);
      assertThat(productEntity.getName()).isEqualTo("Hamburguesa");
      assertThat(productEntity.getStock()).isEqualTo(50);
      assertThat(productEntity.getBranch()).isEqualTo(branchEntity);

      verify(branchJpaRepository).getReferenceById(branchId);
    }

    @Test
    @DisplayName("Debe retornar null cuando el dominio es null")
    void shouldReturnNullWhenDomainIsNull() {
      // Act
      ProductEntity productEntity = productPersistenceMapper.toEntity(null);

      // Assert
      assertThat(productEntity).isNull();
    }

    @Test
    @DisplayName("Debe mapear producto sin ID (nuevo)")
    void shouldMapProductWithoutId() {
      // Arrange
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder().name("Producto Nuevo").stock(100).branchId(branchId).build();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      given(branchJpaRepository.getReferenceById(branchId)).willReturn(branchEntity);

      // Act
      ProductEntity productEntity = productPersistenceMapper.toEntity(product);

      // Assert
      assertThat(productEntity).isNotNull();
      assertThat(productEntity.getId()).isNull();
      assertThat(productEntity.getName()).isEqualTo("Producto Nuevo");
      assertThat(productEntity.getStock()).isEqualTo(100);
    }

    @Test
    @DisplayName("Debe mapear producto sin branchId")
    void shouldMapProductWithoutBranchId() {
      // Arrange
      UUID productId = UUID.randomUUID();

      Product product =
          Product.builder().id(productId).name("Producto Sin Sucursal").stock(10).build();

      // Act
      ProductEntity productEntity = productPersistenceMapper.toEntity(product);

      // Assert
      assertThat(productEntity).isNotNull();
      assertThat(productEntity.getBranch()).isNull();
    }

    @Test
    @DisplayName("Debe establecer la relación con BranchEntity correctamente")
    void shouldSetBranchEntityRelationshipCorrectly() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder().id(productId).name("Papas Fritas").stock(75).branchId(branchId).build();

      BranchEntity branchEntity = BranchEntity.builder().name("Sucursal Centro").build();
      branchEntity.setId(branchId);

      given(branchJpaRepository.getReferenceById(branchId)).willReturn(branchEntity);

      // Act
      ProductEntity productEntity = productPersistenceMapper.toEntity(product);

      // Assert
      assertThat(productEntity.getBranch()).isNotNull();
      assertThat(productEntity.getBranch().getId()).isEqualTo(branchId);
      assertThat(productEntity.getBranch().getName()).isEqualTo("Sucursal Centro");
    }

    @Test
    @DisplayName("Debe usar getReferenceById del repositorio para la relación")
    void shouldUseGetReferenceByIdForRelationship() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product product =
          Product.builder()
              .id(productId)
              .name("Producto Test")
              .stock(20)
              .branchId(branchId)
              .build();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      given(branchJpaRepository.getReferenceById(branchId)).willReturn(branchEntity);

      // Act
      productPersistenceMapper.toEntity(product);

      // Assert
      verify(branchJpaRepository).getReferenceById(branchId);
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

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      given(branchJpaRepository.getReferenceById(branchId)).willReturn(branchEntity);

      // Act
      ProductEntity productEntity = productPersistenceMapper.toEntity(product);

      // Assert
      assertThat(productEntity.getStock()).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("Casos de integración - Mapeo bidireccional")
  class IntegrationTests {

    @Test
    @DisplayName("Debe mantener consistencia en mapeo bidireccional")
    void shouldMaintainConsistencyInBidirectionalMapping() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      BranchEntity branchEntity = BranchEntity.builder().name("Sucursal Test").build();
      branchEntity.setId(branchId);

      ProductEntity originalEntity =
          ProductEntity.builder().name("Hamburguesa").stock(50).branch(branchEntity).build();
      originalEntity.setId(productId);

      given(branchJpaRepository.getReferenceById(branchId)).willReturn(branchEntity);

      // Act - Mapeo de ida y vuelta
      Product domain = productPersistenceMapper.toDomain(originalEntity);
      ProductEntity mappedEntity = productPersistenceMapper.toEntity(domain);

      // Assert - Verificar que los datos se mantienen
      assertThat(mappedEntity.getId()).isEqualTo(productId);
      assertThat(mappedEntity.getName()).isEqualTo("Hamburguesa");
      assertThat(mappedEntity.getStock()).isEqualTo(50);
      assertThat(mappedEntity.getBranch().getId()).isEqualTo(branchId);
    }

    @Test
    @DisplayName("Debe manejar correctamente productos con nombres largos")
    void shouldHandleProductsWithLongNames() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      String longName = "Hamburguesa Especial con Queso Doble y Tocino Extra con Vegetales";

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      ProductEntity productEntity =
          ProductEntity.builder().name(longName).stock(25).branch(branchEntity).build();
      productEntity.setId(productId);

      // Act
      Product product = productPersistenceMapper.toDomain(productEntity);

      // Assert
      assertThat(product.getName()).isEqualTo(longName);
      assertThat(product.getName().length()).isGreaterThan(50);
    }

    @Test
    @DisplayName("Debe preservar todos los campos en el mapeo completo")
    void shouldPreserveAllFieldsInCompleteMapping() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Product originalProduct =
          Product.builder()
              .id(productId)
              .name("Pizza Especial")
              .stock(30)
              .branchId(branchId)
              .build();

      BranchEntity branchEntity = BranchEntity.builder().build();
      branchEntity.setId(branchId);

      given(branchJpaRepository.getReferenceById(branchId)).willReturn(branchEntity);

      // Act
      ProductEntity entity = productPersistenceMapper.toEntity(originalProduct);
      Product mappedProduct = productPersistenceMapper.toDomain(entity);

      // Assert - Todos los campos deben mantenerse
      assertThat(mappedProduct.getId()).isEqualTo(originalProduct.getId());
      assertThat(mappedProduct.getName()).isEqualTo(originalProduct.getName());
      assertThat(mappedProduct.getStock()).isEqualTo(originalProduct.getStock());
      assertThat(mappedProduct.getBranchId()).isEqualTo(originalProduct.getBranchId());
    }
  }
}
