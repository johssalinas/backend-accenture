package com.accenture.franchise.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidad JPA para Franquicia. */
@Entity
@Table(name = "franchises")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseEntity extends BaseEntity {

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @OneToMany(
      mappedBy = "franchise",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Builder.Default
  private List<BranchEntity> branches = new ArrayList<>();

  /** Agrega una sucursal a la franquicia. */
  public void addBranch(BranchEntity branch) {
    branches.add(branch);
    branch.setFranchise(this);
  }

  /** Elimina una sucursal de la franquicia. */
  public void removeBranch(BranchEntity branch) {
    branches.remove(branch);
    branch.setFranchise(null);
  }
}
