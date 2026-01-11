package com.accenture.franchise.infrastructure.adapter.output.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.accenture.franchise.domain.model.Branch;
import com.accenture.franchise.domain.model.Franchise;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.BranchEntity;
import com.accenture.franchise.infrastructure.adapter.output.persistence.entity.FranchiseEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link FranchisePersistenceMapper}.
 *
 * <p>Siguiendo mejores prácticas:
 *
 * <ul>
 *   <li>Uso de Mockito para mocks de mappers dependientes
 *   <li>Patrón AAA (Arrange-Act-Assert)
 *   <li>Tests independientes y aislados
 *   <li>Verificación de mapeo completo de campos
 *   <li>Tests organizados con @Nested para agrupar casos relacionados
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FranchisePersistenceMapper - Pruebas Unitarias")
class FranchisePersistenceMapperTest {

  @Mock private BranchPersistenceMapper branchMapper;

  @InjectMocks private FranchisePersistenceMapper franchisePersistenceMapper;

  @Nested
  @DisplayName("toDomain - Mapeo de FranchiseEntity a Franchise")
  class ToDomainTests {

    @Test
    @DisplayName("Debe mapear correctamente una entidad de franquicia a dominio")
    void shouldMapFranchiseEntityToDomain() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("McDonald's").build();
      franchiseEntity.setId(franchiseId);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise).isNotNull();
      assertThat(franchise.getId()).isEqualTo(franchiseId);
      assertThat(franchise.getName()).isEqualTo("McDonald's");
    }

    @Test
    @DisplayName("Debe retornar null cuando la entidad es null")
    void shouldReturnNullWhenEntityIsNull() {
      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(null);

      // Assert
      assertThat(franchise).isNull();
    }

    @Test
    @DisplayName("Debe mapear franquicia con sucursales")
    void shouldMapFranchiseWithBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("Burger King").build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity = BranchEntity.builder().name("Sucursal Centro").build();
      branchEntity.setId(branchId);
      franchiseEntity.addBranch(branchEntity);

      Branch branchDomain =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      given(branchMapper.toDomain(any(BranchEntity.class))).willReturn(branchDomain);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise).isNotNull();
      assertThat(franchise.getBranches()).hasSize(1);
      assertThat(franchise.getBranches().get(0).getName()).isEqualTo("Sucursal Centro");

      verify(branchMapper).toDomain(any(BranchEntity.class));
    }

    @Test
    @DisplayName("Debe mapear franquicia sin sucursales")
    void shouldMapFranchiseWithoutBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("KFC").build();
      franchiseEntity.setId(franchiseId);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise).isNotNull();
      assertThat(franchise.getBranches()).isEmpty();
    }

    @Test
    @DisplayName("Debe mapear múltiples sucursales correctamente")
    void shouldMapMultipleBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("Pizza Hut").build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branch1 = BranchEntity.builder().name("Sucursal Norte").build();
      branch1.setId(UUID.randomUUID());

      BranchEntity branch2 = BranchEntity.builder().name("Sucursal Sur").build();
      branch2.setId(UUID.randomUUID());

      franchiseEntity.addBranch(branch1);
      franchiseEntity.addBranch(branch2);

      Branch branchDomain1 =
          Branch.builder()
              .id(branch1.getId())
              .name("Sucursal Norte")
              .franchiseId(franchiseId)
              .build();

      Branch branchDomain2 =
          Branch.builder()
              .id(branch2.getId())
              .name("Sucursal Sur")
              .franchiseId(franchiseId)
              .build();

      given(branchMapper.toDomain(branch1)).willReturn(branchDomain1);
      given(branchMapper.toDomain(branch2)).willReturn(branchDomain2);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise.getBranches()).hasSize(2);
      assertThat(franchise.getBranches().get(0).getName()).isEqualTo("Sucursal Norte");
      assertThat(franchise.getBranches().get(1).getName()).isEqualTo("Sucursal Sur");

      verify(branchMapper, times(2)).toDomain(any(BranchEntity.class));
    }

    @Test
    @DisplayName("Debe preservar nombres con caracteres especiales")
    void shouldPreserveNamesWithSpecialCharacters() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity =
          FranchiseEntity.builder().name("McDonald's & Burger King").build();
      franchiseEntity.setId(franchiseId);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise.getName()).isEqualTo("McDonald's & Burger King");
    }

    @Test
    @DisplayName("Debe manejar nombres largos correctamente")
    void shouldHandleLongNames() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      String longName =
          "Super Mega Ultimate Restaurant Chain International Corporation LLC Holdings";

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name(longName).build();
      franchiseEntity.setId(franchiseId);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise.getName()).isEqualTo(longName);
      assertThat(franchise.getName().length()).isGreaterThan(50);
    }
  }

  @Nested
  @DisplayName("toEntity - Mapeo de Franchise a FranchiseEntity")
  class ToEntityTests {

    @Test
    @DisplayName("Debe mapear correctamente una franquicia de dominio a entidad")
    void shouldMapFranchiseDomainToEntity() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("McDonald's").build();

      // Act
      FranchiseEntity franchiseEntity = franchisePersistenceMapper.toEntity(franchise);

      // Assert
      assertThat(franchiseEntity).isNotNull();
      assertThat(franchiseEntity.getId()).isEqualTo(franchiseId);
      assertThat(franchiseEntity.getName()).isEqualTo("McDonald's");
    }

    @Test
    @DisplayName("Debe retornar null cuando el dominio es null")
    void shouldReturnNullWhenDomainIsNull() {
      // Act
      FranchiseEntity franchiseEntity = franchisePersistenceMapper.toEntity(null);

      // Assert
      assertThat(franchiseEntity).isNull();
    }

    @Test
    @DisplayName("Debe mapear franquicia sin ID (nueva)")
    void shouldMapFranchiseWithoutId() {
      // Arrange
      Franchise franchise = Franchise.builder().name("Nueva Franquicia").build();

      // Act
      FranchiseEntity franchiseEntity = franchisePersistenceMapper.toEntity(franchise);

      // Assert
      assertThat(franchiseEntity).isNotNull();
      assertThat(franchiseEntity.getId()).isNull();
      assertThat(franchiseEntity.getName()).isEqualTo("Nueva Franquicia");
    }

    @Test
    @DisplayName("Debe mapear franquicia con sucursales")
    void shouldMapFranchiseWithBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Burger King").build();
      franchise.addBranch(branch);

      BranchEntity branchEntity = BranchEntity.builder().name("Sucursal Centro").build();
      branchEntity.setId(branchId);

      given(branchMapper.toEntity(any(Branch.class))).willReturn(branchEntity);

      // Act
      FranchiseEntity franchiseEntity = franchisePersistenceMapper.toEntity(franchise);

      // Assert
      assertThat(franchiseEntity).isNotNull();
      assertThat(franchiseEntity.getBranches()).hasSize(1);

      verify(branchMapper).toEntity(any(Branch.class));
    }

    @Test
    @DisplayName("Debe mapear franquicia sin sucursales")
    void shouldMapFranchiseWithoutBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Franchise franchise = Franchise.builder().id(franchiseId).name("KFC").build();

      // Act
      FranchiseEntity franchiseEntity = franchisePersistenceMapper.toEntity(franchise);

      // Assert
      assertThat(franchiseEntity).isNotNull();
      assertThat(franchiseEntity.getBranches()).isEmpty();
    }

    @Test
    @DisplayName("Debe mapear múltiples sucursales correctamente")
    void shouldMapMultipleBranches() {
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

      Franchise franchise = Franchise.builder().id(franchiseId).name("Pizza Hut").build();
      franchise.addBranch(branch1);
      franchise.addBranch(branch2);

      BranchEntity branchEntity1 = BranchEntity.builder().name("Sucursal Norte").build();
      branchEntity1.setId(branch1.getId());

      BranchEntity branchEntity2 = BranchEntity.builder().name("Sucursal Sur").build();
      branchEntity2.setId(branch2.getId());

      given(branchMapper.toEntity(branch1)).willReturn(branchEntity1);
      given(branchMapper.toEntity(branch2)).willReturn(branchEntity2);

      // Act
      FranchiseEntity franchiseEntity = franchisePersistenceMapper.toEntity(franchise);

      // Assert
      assertThat(franchiseEntity.getBranches()).hasSize(2);

      verify(branchMapper, times(2)).toEntity(any(Branch.class));
    }

    @Test
    @DisplayName("Debe establecer relación bidireccional con sucursales")
    void shouldSetBidirectionalRelationshipWithBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      Branch branch =
          Branch.builder().id(branchId).name("Sucursal Centro").franchiseId(franchiseId).build();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Domino's Pizza").build();
      franchise.addBranch(branch);

      BranchEntity branchEntity = BranchEntity.builder().name("Sucursal Centro").build();
      branchEntity.setId(branchId);

      given(branchMapper.toEntity(any(Branch.class))).willReturn(branchEntity);

      // Act
      FranchiseEntity franchiseEntity = franchisePersistenceMapper.toEntity(franchise);

      // Assert
      assertThat(franchiseEntity.getBranches()).hasSize(1);
      assertThat(franchiseEntity.getBranches().get(0).getFranchise()).isEqualTo(franchiseEntity);
    }
  }

  @Nested
  @DisplayName("Casos de integración - Mapeo bidireccional")
  class IntegrationTests {

    @Test
    @DisplayName("Debe mantener consistencia en mapeo bidireccional")
    void shouldMaintainConsistencyInBidirectionalMapping() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity originalEntity = FranchiseEntity.builder().name("Subway").build();
      originalEntity.setId(franchiseId);

      // Act - Mapeo de ida y vuelta
      Franchise domain = franchisePersistenceMapper.toDomain(originalEntity);
      FranchiseEntity mappedEntity = franchisePersistenceMapper.toEntity(domain);

      // Assert - Verificar que los datos se mantienen
      assertThat(mappedEntity.getId()).isEqualTo(franchiseId);
      assertThat(mappedEntity.getName()).isEqualTo("Subway");
    }

    @Test
    @DisplayName("Debe preservar todos los campos en el mapeo completo")
    void shouldPreserveAllFieldsInCompleteMapping() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Franchise originalFranchise = Franchise.builder().id(franchiseId).name("Starbucks").build();

      // Act
      FranchiseEntity entity = franchisePersistenceMapper.toEntity(originalFranchise);
      Franchise mappedFranchise = franchisePersistenceMapper.toDomain(entity);

      // Assert - Todos los campos deben mantenerse
      assertThat(mappedFranchise.getId()).isEqualTo(originalFranchise.getId());
      assertThat(mappedFranchise.getName()).isEqualTo(originalFranchise.getName());
    }

    @Test
    @DisplayName("Debe mapear correctamente jerarquía completa con sucursales")
    void shouldMapCompleteHierarchyWithBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();

      FranchiseEntity franchiseEntity = FranchiseEntity.builder().name("Wendy's").build();
      franchiseEntity.setId(franchiseId);

      BranchEntity branchEntity = BranchEntity.builder().name("Sucursal Principal").build();
      branchEntity.setId(branchId);
      franchiseEntity.addBranch(branchEntity);

      Branch branchDomain =
          Branch.builder().id(branchId).name("Sucursal Principal").franchiseId(franchiseId).build();

      given(branchMapper.toDomain(any(BranchEntity.class))).willReturn(branchDomain);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise).isNotNull();
      assertThat(franchise.getBranches()).hasSize(1);
      assertThat(franchise.getBranches().get(0).getFranchiseId()).isEqualTo(franchiseId);
    }

    @Test
    @DisplayName("Debe manejar franquicias vacías correctamente")
    void shouldHandleEmptyFranchisesCorrectly() {
      // Arrange
      Franchise emptyFranchise =
          Franchise.builder().id(UUID.randomUUID()).name("Empty Franchise").build();

      // Act
      FranchiseEntity entity = franchisePersistenceMapper.toEntity(emptyFranchise);
      Franchise mappedFranchise = franchisePersistenceMapper.toDomain(entity);

      // Assert
      assertThat(mappedFranchise).isNotNull();
      assertThat(mappedFranchise.getBranches()).isEmpty();
    }

    @Test
    @DisplayName("Debe preservar nombres con caracteres Unicode")
    void shouldPreserveNamesWithUnicodeCharacters() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      FranchiseEntity franchiseEntity =
          FranchiseEntity.builder().name("Café Gourmet ☕ & Restaurante 🍔").build();
      franchiseEntity.setId(franchiseId);

      // Act
      Franchise franchise = franchisePersistenceMapper.toDomain(franchiseEntity);

      // Assert
      assertThat(franchise.getName()).isEqualTo("Café Gourmet ☕ & Restaurante 🍔");
    }

    @Test
    @DisplayName("Debe validar la integridad del mapeo con múltiples sucursales")
    void shouldValidateMappingIntegrityWithMultipleBranches() {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      Branch branch1 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal 1")
              .franchiseId(franchiseId)
              .build();

      Branch branch2 =
          Branch.builder()
              .id(UUID.randomUUID())
              .name("Sucursal 2")
              .franchiseId(franchiseId)
              .build();

      Franchise franchise = Franchise.builder().id(franchiseId).name("Taco Bell").build();
      franchise.addBranch(branch1);
      franchise.addBranch(branch2);

      BranchEntity branchEntity1 = BranchEntity.builder().name("Sucursal 1").build();
      branchEntity1.setId(branch1.getId());

      BranchEntity branchEntity2 = BranchEntity.builder().name("Sucursal 2").build();
      branchEntity2.setId(branch2.getId());

      given(branchMapper.toEntity(branch1)).willReturn(branchEntity1);
      given(branchMapper.toEntity(branch2)).willReturn(branchEntity2);

      // Act
      FranchiseEntity entity = franchisePersistenceMapper.toEntity(franchise);

      // Assert
      assertThat(entity.getBranches()).hasSize(2);
      assertThat(entity.getBranches().get(0).getFranchise()).isEqualTo(entity);
      assertThat(entity.getBranches().get(1).getFranchise()).isEqualTo(entity);
    }
  }
}
