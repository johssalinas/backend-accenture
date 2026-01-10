package com.accenture.franchise.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidad JPA para Producto. */
@Entity
@Table(
    name = "products",
    uniqueConstraints =
        @UniqueConstraint(
            name = "products_unique_name_per_branch",
            columnNames = {"branch_id", "name"}),
    indexes = {
      @Index(name = "idx_products_branch_id", columnList = "branch_id"),
      @Index(name = "idx_products_name", columnList = "name"),
      @Index(name = "idx_products_stock", columnList = "stock")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private Integer stock;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", nullable = false)
  private BranchEntity branch;
}
