package com.accenture.franchise.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidad JPA para Sucursal. */
@Entity
@Table(
    name = "branches",
    uniqueConstraints =
        @UniqueConstraint(
            name = "branches_unique_name_per_franchise",
            columnNames = {"franchise_id", "name"}),
    indexes = {
      @Index(name = "idx_branches_franchise_id", columnList = "franchise_id"),
      @Index(name = "idx_branches_name", columnList = "name")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "franchise_id", nullable = false)
  private FranchiseEntity franchise;

  @OneToMany(
      mappedBy = "branch",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Builder.Default
  private List<ProductEntity> products = new ArrayList<>();

  /** Agrega un producto a la sucursal. */
  public void addProduct(ProductEntity product) {
    products.add(product);
    product.setBranch(this);
  }

  /** Elimina un producto de la sucursal. */
  public void removeProduct(ProductEntity product) {
    products.remove(product);
    product.setBranch(null);
  }
}
