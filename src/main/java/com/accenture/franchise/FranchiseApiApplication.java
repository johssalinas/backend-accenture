package com.accenture.franchise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Aplicación principal de la API REST para gestión de franquicias, sucursales y productos.
 *
 * <p>Tecnologías:
 *
 * <ul>
 *   <li>Spring Boot 4 con Java 25
 *   <li>PostgreSQL para persistencia
 *   <li>Redis para caché
 *   <li>Clean Architecture
 *   <li>OpenAPI/Swagger para documentación
 * </ul>
 */
@SpringBootApplication
@EnableCaching
public class FranchiseApiApplication {

  /** Punto de entrada de la aplicación. */
  public static void main(String[] args) {
    SpringApplication.run(FranchiseApiApplication.class, args);
  }
}
