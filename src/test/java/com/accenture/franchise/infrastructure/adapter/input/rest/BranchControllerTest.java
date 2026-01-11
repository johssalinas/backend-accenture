package com.accenture.franchise.infrastructure.adapter.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.accenture.franchise.application.dto.BranchResponse;
import com.accenture.franchise.application.dto.CreateBranchRequest;
import com.accenture.franchise.application.dto.UpdateBranchNameRequest;
import com.accenture.franchise.application.usecase.branch.AddBranchToFranchiseUseCase;
import com.accenture.franchise.application.usecase.branch.UpdateBranchNameUseCase;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
 * Pruebas de integración para {@link BranchController}.
 *
 * <p>Utiliza @WebMvcTest para pruebas slice de la capa web, enfocándose en el controlador sin
 * cargar el contexto completo de Spring.
 */
@WebMvcTest(BranchController.class)
@DisplayName("BranchController - Pruebas de Integración")
class BranchControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private CacheManager cacheManager;

  @MockitoBean private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

  @MockitoBean private UpdateBranchNameUseCase updateBranchNameUseCase;

  @Nested
  @DisplayName("POST /api/v1/branches - Agregar Sucursal")
  class AddBranchTests {

    @Test
    @DisplayName("Debe agregar sucursal exitosamente y retornar 201")
    void shouldAddBranchSuccessfully() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Sucursal Centro";
      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      UUID branchId = UUID.randomUUID();
      BranchResponse response =
          new BranchResponse(branchId, branchName, franchiseId, new ArrayList<>());

      given(addBranchToFranchiseUseCase.execute(any(CreateBranchRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/branches")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(branchId.toString()))
          .andExpect(jsonPath("$.name").value(branchName))
          .andExpect(jsonPath("$.franchiseId").value(franchiseId.toString()))
          .andExpect(jsonPath("$.products").isArray())
          .andExpect(jsonPath("$.products").isEmpty());

      verify(addBranchToFranchiseUseCase).execute(any(CreateBranchRequest.class));
    }

    @Test
    @DisplayName("Debe retornar 409 cuando el nombre ya existe en la franquicia")
    void shouldReturn409WhenNameAlreadyExistsInFranchise() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Sucursal Centro";
      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      given(addBranchToFranchiseUseCase.execute(any(CreateBranchRequest.class)))
          .willThrow(
              new BusinessRuleViolationException(
                  "Branch with name already exists in this franchise: " + branchName));

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/branches")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la franquicia no existe")
    void shouldReturn404WhenFranchiseNotFound() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      CreateBranchRequest request = new CreateBranchRequest(franchiseId, "Sucursal Centro");

      given(addBranchToFranchiseUseCase.execute(any(CreateBranchRequest.class)))
          .willThrow(new EntityNotFoundException("Franchise", franchiseId));

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/branches")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el request es inválido - sin nombre")
    void shouldReturn400WhenRequestIsInvalidWithoutName() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String invalidJson = String.format("{\"franchiseId\":\"%s\"}", franchiseId);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/branches").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el request es inválido - sin franchiseId")
    void shouldReturn400WhenRequestIsInvalidWithoutFranchiseId() throws Exception {
      // Arrange
      String invalidJson = "{\"name\":\"Sucursal Centro\"}";

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/branches").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el nombre está vacío")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      CreateBranchRequest request = new CreateBranchRequest(franchiseId, "");

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/branches")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/branches/{branchId}/name - Actualizar Nombre")
  class UpdateBranchNameTests {

    @Test
    @DisplayName("Debe actualizar nombre exitosamente y retornar 200")
    void shouldUpdateBranchNameSuccessfully() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UUID franchiseId = UUID.randomUUID();
      String newName = "Sucursal Norte";

      UpdateBranchNameRequest request = new UpdateBranchNameRequest(newName);

      BranchResponse response =
          new BranchResponse(branchId, newName, franchiseId, new ArrayList<>());

      given(updateBranchNameUseCase.execute(eq(branchId), any(UpdateBranchNameRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/branches/{branchId}/name", branchId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(branchId.toString()))
          .andExpect(jsonPath("$.name").value(newName))
          .andExpect(jsonPath("$.franchiseId").value(franchiseId.toString()));

      verify(updateBranchNameUseCase).execute(eq(branchId), any(UpdateBranchNameRequest.class));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la sucursal no existe")
    void shouldReturn404WhenBranchNotFound() throws Exception {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      UpdateBranchNameRequest request = new UpdateBranchNameRequest("Nuevo Nombre");

      given(updateBranchNameUseCase.execute(eq(nonExistentId), any(UpdateBranchNameRequest.class)))
          .willThrow(new EntityNotFoundException("Branch", nonExistentId));

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/branches/{branchId}/name", nonExistentId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el nombre es inválido")
    void shouldReturn400WhenNameIsInvalid() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();

      // Act & Assert - Enviar request sin nombre
      mockMvc
          .perform(
              patch("/api/v1/branches/{branchId}/name", branchId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el nombre está vacío")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      UpdateBranchNameRequest request = new UpdateBranchNameRequest("");

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/branches/{branchId}/name", branchId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe manejar UUID inválido correctamente")
    void shouldHandleInvalidUUIDCorrectly() throws Exception {
      // Arrange
      UpdateBranchNameRequest request = new UpdateBranchNameRequest("Nuevo Nombre");

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/branches/{branchId}/name", "invalid-uuid")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("Validaciones de Content-Type y formato")
  class ContentTypeAndFormatValidationTests {

    @Test
    @DisplayName("Debe rechazar request sin Content-Type")
    void shouldRejectRequestWithoutContentType() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      CreateBranchRequest request = new CreateBranchRequest(franchiseId, "Test");

      // Act & Assert
      mockMvc
          .perform(post("/api/v1/branches").content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe aceptar solo application/json como Content-Type")
    void shouldAcceptOnlyApplicationJsonContentType() throws Exception {
      // Arrange
      UUID franchiseId = UUID.randomUUID();
      String branchName = "Test Branch";
      CreateBranchRequest request = new CreateBranchRequest(franchiseId, branchName);

      UUID branchId = UUID.randomUUID();
      BranchResponse response =
          new BranchResponse(branchId, branchName, franchiseId, new ArrayList<>());

      given(addBranchToFranchiseUseCase.execute(any(CreateBranchRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/branches")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());
    }
  }
}
