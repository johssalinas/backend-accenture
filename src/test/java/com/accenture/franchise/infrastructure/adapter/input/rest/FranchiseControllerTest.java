package com.accenture.franchise.infrastructure.adapter.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.accenture.franchise.application.dto.CreateFranchiseRequest;
import com.accenture.franchise.application.dto.FranchiseResponse;
import com.accenture.franchise.application.dto.ProductStockResponse;
import com.accenture.franchise.application.dto.UpdateFranchiseNameRequest;
import com.accenture.franchise.application.usecase.franchise.CreateFranchiseUseCase;
import com.accenture.franchise.application.usecase.franchise.GetFranchiseUseCase;
import com.accenture.franchise.application.usecase.franchise.GetTopStockProductsByFranchiseUseCase;
import com.accenture.franchise.application.usecase.franchise.UpdateFranchiseNameUseCase;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas de integración para {@link FranchiseController}.
 *
 * <p>Utiliza @WebMvcTest para pruebas slice de la capa web, enfocándose en el controlador sin
 * cargar el contexto completo de Spring.
 */
@WebMvcTest(FranchiseController.class)
@DisplayName("FranchiseController - Pruebas de Integración")
class FranchiseControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private CacheManager cacheManager;

  @MockitoBean private CreateFranchiseUseCase createFranchiseUseCase;

  @MockitoBean private GetFranchiseUseCase getFranchiseUseCase;

  @MockitoBean private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

  @MockitoBean private GetTopStockProductsByFranchiseUseCase getTopStockProductsByFranchiseUseCase;

  @Nested
  @DisplayName("POST /api/v1/franchises - Crear Franquicia")
  class CreateFranchiseTests {

    @Test
    @DisplayName("Debe crear franquicia exitosamente y retornar 201")
    void shouldCreateFranchiseSuccessfully() throws Exception {
      // Arrange
      String franchiseName = "McDonald's";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      UUID franchiseId = UUID.randomUUID();
      FranchiseResponse response =
          new FranchiseResponse(franchiseId, franchiseName, new ArrayList<>());

      given(createFranchiseUseCase.execute(any(CreateFranchiseRequest.class))).willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/franchises")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(franchiseId.toString()))
          .andExpect(jsonPath("$.name").value(franchiseName))
          .andExpect(jsonPath("$.branches").isArray())
          .andExpect(jsonPath("$.branches").isEmpty());

      verify(createFranchiseUseCase).execute(any(CreateFranchiseRequest.class));
    }

    @Test
    @DisplayName("Debe retornar 409 cuando el nombre ya existe")
    void shouldReturn409WhenNameAlreadyExists() throws Exception {
      // Arrange
      String franchiseName = "McDonald's";
      CreateFranchiseRequest request = new CreateFranchiseRequest(franchiseName);

      given(createFranchiseUseCase.execute(any(CreateFranchiseRequest.class)))
          .willThrow(
              new BusinessRuleViolationException(
                  "Franchise with name already exists: " + franchiseName));

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/franchises")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el request es inválido")
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
      // Act & Assert - Enviar request vacío
      mockMvc
          .perform(post("/api/v1/franchises").contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/franchises/{franchiseId} - Obtener Franquicia")
  class GetFranchiseTests {

    @Test
    @DisplayName("Debe obtener franquicia exitosamente y retornar 200")
    void shouldGetFranchiseSuccessfully() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String franchiseName = "McDonald's";

      FranchiseResponse response =
          new FranchiseResponse(franchiseId, franchiseName, new ArrayList<>());

      given(getFranchiseUseCase.execute(franchiseId)).willReturn(response);

      // Act & Assert
      mockMvc
          .perform(get("/api/v1/franchises/{franchiseId}", franchiseId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(franchiseId.toString()))
          .andExpect(jsonPath("$.name").value(franchiseName))
          .andExpect(jsonPath("$.branches").isArray());

      verify(getFranchiseUseCase).execute(franchiseId);
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la franquicia no existe")
    void shouldReturn404WhenFranchiseNotFound() throws Exception {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      given(getFranchiseUseCase.execute(nonExistentId))
          .willThrow(new EntityNotFoundException("Franchise", nonExistentId));

      // Act & Assert
      mockMvc
          .perform(get("/api/v1/franchises/{franchiseId}", nonExistentId))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe manejar UUID inválido correctamente")
    void shouldHandleInvalidUUIDCorrectly() throws Exception {
      // Act & Assert
      mockMvc
          .perform(get("/api/v1/franchises/{franchiseId}", "invalid-uuid"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/franchises/{franchiseId}/name - Actualizar Nombre")
  class UpdateFranchiseNameTests {

    @Test
    @DisplayName("Debe actualizar nombre exitosamente y retornar 200")
    void shouldUpdateFranchiseNameSuccessfully() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String newName = "New Franchise Name";

      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest(newName);

      FranchiseResponse response = new FranchiseResponse(franchiseId, newName, new ArrayList<>());

      given(
              updateFranchiseNameUseCase.execute(
                  eq(franchiseId), any(UpdateFranchiseNameRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/franchises/{franchiseId}/name", franchiseId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(franchiseId.toString()))
          .andExpect(jsonPath("$.name").value(newName));

      verify(updateFranchiseNameUseCase)
          .execute(eq(franchiseId), any(UpdateFranchiseNameRequest.class));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la franquicia no existe")
    void shouldReturn404WhenFranchiseNotFound() throws Exception {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest("New Name");

      given(
              updateFranchiseNameUseCase.execute(
                  eq(nonExistentId), any(UpdateFranchiseNameRequest.class)))
          .willThrow(new EntityNotFoundException("Franchise", nonExistentId));

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/franchises/{franchiseId}/name", nonExistentId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el nombre es inválido")
    void shouldReturn400WhenNameIsInvalid() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      // Act & Assert - Enviar request sin nombre
      mockMvc
          .perform(
              patch("/api/v1/franchises/{franchiseId}/name", franchiseId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName(
      "GET /api/v1/franchises/{franchiseId}/top-stock-products - Productos con Mayor Stock")
  class GetTopStockProductsTests {

    @Test
    @DisplayName("Debe obtener productos con mayor stock exitosamente")
    void shouldGetTopStockProductsSuccessfully() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();

      ProductStockResponse productStock =
          new ProductStockResponse(productId, "Hamburguesa", 100, branchId, "Sucursal Centro");

      List<ProductStockResponse> response = List.of(productStock);

      given(getTopStockProductsByFranchiseUseCase.execute(franchiseId)).willReturn(response);

      // Act & Assert
      mockMvc
          .perform(get("/api/v1/franchises/{franchiseId}/top-stock-products", franchiseId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].productName").value("Hamburguesa"))
          .andExpect(jsonPath("$[0].stock").value(100))
          .andExpect(jsonPath("$[0].branchName").value("Sucursal Centro"));

      verify(getTopStockProductsByFranchiseUseCase).execute(franchiseId);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay productos")
    void shouldReturnEmptyListWhenNoProducts() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      given(getTopStockProductsByFranchiseUseCase.execute(franchiseId))
          .willReturn(new ArrayList<>());

      // Act & Assert
      mockMvc
          .perform(get("/api/v1/franchises/{franchiseId}/top-stock-products", franchiseId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la franquicia no existe")
    void shouldReturn404WhenFranchiseNotFound() throws Exception {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      given(getTopStockProductsByFranchiseUseCase.execute(nonExistentId))
          .willThrow(new EntityNotFoundException("Franchise", nonExistentId));

      // Act & Assert
      mockMvc
          .perform(get("/api/v1/franchises/{franchiseId}/top-stock-products", nonExistentId))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe manejar múltiples productos correctamente")
    void shouldHandleMultipleProductsCorrectly() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();

      ProductStockResponse product1 =
          new ProductStockResponse(
              UUID.randomUUID(), "Hamburguesa", 100, UUID.randomUUID(), "Sucursal Centro");

      ProductStockResponse product2 =
          new ProductStockResponse(
              UUID.randomUUID(), "Pizza", 75, UUID.randomUUID(), "Sucursal Norte");

      List<ProductStockResponse> response = List.of(product1, product2);

      given(getTopStockProductsByFranchiseUseCase.execute(franchiseId)).willReturn(response);

      // Act & Assert
      mockMvc
          .perform(get("/api/v1/franchises/{franchiseId}/top-stock-products", franchiseId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].productName").value("Hamburguesa"))
          .andExpect(jsonPath("$[1].productName").value("Pizza"));
    }
  }

  @Nested
  @DisplayName("Validaciones de Content-Type y formato")
  class ContentTypeAndFormatValidationTests {

    @Test
    @DisplayName("Debe rechazar request sin Content-Type")
    void shouldRejectRequestWithoutContentType() throws Exception {
      // Arrange
      CreateFranchiseRequest request = new CreateFranchiseRequest("Test");

      // Act & Assert
      mockMvc
          .perform(post("/api/v1/franchises").content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe aceptar solo application/json como Content-Type")
    void shouldAcceptOnlyApplicationJsonContentType() throws Exception {
      // Arrange
      CreateFranchiseRequest request = new CreateFranchiseRequest("Test");

      UUID franchiseId = UUID.randomUUID();
      FranchiseResponse response = new FranchiseResponse(franchiseId, "Test", new ArrayList<>());

      given(createFranchiseUseCase.execute(any(CreateFranchiseRequest.class))).willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/franchises")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());
    }
  }
}
