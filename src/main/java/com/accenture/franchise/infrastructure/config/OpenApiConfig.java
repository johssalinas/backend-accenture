package com.accenture.franchise.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración de OpenAPI/Swagger. */
@Configuration
public class OpenApiConfig {

  @Value("${spring.application.name}")
  private String applicationName;

  /** Configura la documentación de OpenAPI/Swagger para la API. */
  @Bean
  public OpenAPI openApiDocumentation() {
    return new OpenAPI()
        .info(
            new Info()
                .title("API de Gestión de Franquicias")
                .description("API REST para gestión de franquicias, sucursales y productos")
                .version("1.0.0")
                .contact(new Contact().name("Johs Salinas").email("johssalinas2work@gmail.com"))
                .license(
                    new License().name("Licencia MIT").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Servidor de Desarrollo"),
                new Server().url("http://franchise-api-alb-2042942561.us-east-2.elb.amazonaws.com/api/v1' || 'http://localhost:8080").description("Servidor de Producción")
            ));
  }
}
