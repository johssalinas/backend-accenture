package com.accenture.franchise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Aplicación principal - Franchise Management API
 * API REST para gestión de franquicias, sucursales y productos
 * 
 * Tecnologías:
 * - Spring Boot 4 con Java 25
 * - PostgreSQL para persistencia
 * - Redis para caché
 * - Clean Architecture
 * - OpenAPI/Swagger para documentación
 */
@SpringBootApplication
@EnableCaching
public class FranchiseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FranchiseApiApplication.class, args);
	}

}
