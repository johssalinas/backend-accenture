package com.accenture.franchise.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.accenture.franchise",
    importOptions = ImportOption.DoNotIncludeTests.class)
public final class CleanArchitectureTest {

  @ArchTest
  static final ArchRule layered_architecture_is_respected =
      layeredArchitecture()
          .consideringOnlyDependenciesInAnyPackage("com.accenture.franchise..")
          .layer("Domain")
          .definedBy("com.accenture.franchise.domain..")
          .layer("Application")
          .definedBy("com.accenture.franchise.application..")
          .layer("Infrastructure")
          .definedBy("com.accenture.franchise.infrastructure..")
          .whereLayer("Domain")
          .mayOnlyBeAccessedByLayers("Application", "Infrastructure")
          .whereLayer("Application")
          .mayOnlyBeAccessedByLayers("Infrastructure")
          .whereLayer("Infrastructure")
          .mayNotBeAccessedByAnyLayer();

  private CleanArchitectureTest() {}
}
