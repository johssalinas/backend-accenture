package com.accenture.franchise.infrastructure.adapter.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.accenture.franchise.application.dto.CreateProductRequest;
import com.accenture.franchise.application.dto.ProductResponse;
import com.accenture.franchise.application.dto.UpdateProductNameRequest;
import com.accenture.franchise.application.dto.UpdateProductStockRequest;
import com.accenture.franchise.application.usecase.product.AddProductToBranchUseCase;
import com.accenture.franchise.application.usecase.product.RemoveProductFromBranchUseCase;
import com.accenture.franchise.application.usecase.product.UpdateProductNameUseCase;
import com.accenture.franchise.application.usecase.product.UpdateProductStockUseCase;
import com.accenture.franchise.domain.exception.BusinessRuleViolationException;
import com.accenture.franchise.domain.exception.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Pruebas de integración para {@link ProductController}.
 *
 * <p>Utiliza @WebMvcTest para pruebas slice de la capa web, enfocándose en el controlador sin
 * cargar el contexto completo de Spring.
 */
@WebMvcTest(ProductController.class)
@DisplayName("ProductController - Pruebas de Integración")
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private CacheManager cacheManager;

  @MockitoBean private AddProductToBranchUseCase addProductToBranchUseCase;

  @MockitoBean private RemoveProductFromBranchUseCase removeProductFromBranchUseCase;

  @MockitoBean private UpdateProductStockUseCase updateProductStockUseCase;

  @MockitoBean private UpdateProductNameUseCase updateProductNameUseCase;

  @Nested
  @DisplayName("POST /api/v1/products - Agregar Producto")
  class AddProductTests {

    @Test
    @DisplayName("Debe agregar producto exitosamente y retornar 201")
    void shouldAddProductSuccessfully() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Hamburguesa";
      Integer stock = 50;
      CreateProductRequest request = new CreateProductRequest(branchId, productName, stock);

      UUID productId = UUID.randomUUID();
      ProductResponse response = new ProductResponse(productId, productName, stock, branchId);

      given(addProductToBranchUseCase.execute(any(CreateProductRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(productId.toString()))
          .andExpect(jsonPath("$.name").value(productName))
          .andExpect(jsonPath("$.stock").value(stock))
          .andExpect(jsonPath("$.branchId").value(branchId.toString()));

      verify(addProductToBranchUseCase).execute(any(CreateProductRequest.class));
    }

    @Test
    @DisplayName("Debe retornar 409 cuando el nombre ya existe en la sucursal")
    void shouldReturn409WhenNameAlreadyExistsInBranch() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Hamburguesa";
      CreateProductRequest request = new CreateProductRequest(branchId, productName, 50);

      given(addProductToBranchUseCase.execute(any(CreateProductRequest.class)))
          .willThrow(
              new BusinessRuleViolationException(
                  "Product with name already exists in this branch: " + productName));

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la sucursal no existe")
    void shouldReturn404WhenBranchNotFound() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      CreateProductRequest request = new CreateProductRequest(branchId, "Hamburguesa", 50);

      given(addProductToBranchUseCase.execute(any(CreateProductRequest.class)))
          .willThrow(new EntityNotFoundException("Branch", branchId));

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el request es inválido - sin nombre")
    void shouldReturn400WhenRequestIsInvalidWithoutName() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String invalidJson = String.format("{\"branchId\":\"%s\",\"stock\":50}", branchId);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el request es inválido - sin branchId")
    void shouldReturn400WhenRequestIsInvalidWithoutBranchId() throws Exception {
      // Arrange
      String invalidJson = "{\"name\":\"Hamburguesa\",\"stock\":50}";

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el request es inválido - sin stock")
    void shouldReturn400WhenRequestIsInvalidWithoutStock() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String invalidJson =
          String.format("{\"branchId\":\"%s\",\"name\":\"Hamburguesa\"}", branchId);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el stock es negativo")
    void shouldReturn400WhenStockIsNegative() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      CreateProductRequest request = new CreateProductRequest(branchId, "Hamburguesa", -10);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe permitir stock cero")
    void shouldAllowZeroStock() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Hamburguesa";
      Integer stock = 0;
      CreateProductRequest request = new CreateProductRequest(branchId, productName, stock);

      UUID productId = UUID.randomUUID();
      ProductResponse response = new ProductResponse(productId, productName, stock, branchId);

      given(addProductToBranchUseCase.execute(any(CreateProductRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.stock").value(0));
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/products/{productId} - Eliminar Producto")
  class RemoveProductTests {

    @Test
    @DisplayName("Debe eliminar producto exitosamente y retornar 204")
    void shouldRemoveProductSuccessfully() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();

      doNothing().when(removeProductFromBranchUseCase).execute(productId);

      // Act & Assert
      mockMvc
          .perform(delete("/api/v1/products/{productId}", productId))
          .andExpect(status().isNoContent());

      verify(removeProductFromBranchUseCase).execute(productId);
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el producto no existe")
    void shouldReturn404WhenProductNotFound() throws Exception {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();

      doThrow(new EntityNotFoundException("Product", nonExistentId))
          .when(removeProductFromBranchUseCase)
          .execute(nonExistentId);

      // Act & Assert
      mockMvc
          .perform(delete("/api/v1/products/{productId}", nonExistentId))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe manejar UUID inválido correctamente")
    void shouldHandleInvalidUUIDCorrectly() throws Exception {
      // Act & Assert
      mockMvc
          .perform(delete("/api/v1/products/{productId}", "invalid-uuid"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/products/{productId}/stock - Actualizar Stock")
  class UpdateProductStockTests {

    @Test
    @DisplayName("Debe actualizar stock exitosamente y retornar 200")
    void shouldUpdateProductStockSuccessfully() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      Integer newStock = 100;

      UpdateProductStockRequest request = new UpdateProductStockRequest(newStock);

      ProductResponse response = new ProductResponse(productId, "Hamburguesa", newStock, branchId);

      given(updateProductStockUseCase.execute(eq(productId), any(UpdateProductStockRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/stock", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(productId.toString()))
          .andExpect(jsonPath("$.stock").value(newStock));

      verify(updateProductStockUseCase)
          .execute(eq(productId), any(UpdateProductStockRequest.class));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el producto no existe")
    void shouldReturn404WhenProductNotFound() throws Exception {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      UpdateProductStockRequest request = new UpdateProductStockRequest(100);

      given(
              updateProductStockUseCase.execute(
                  eq(nonExistentId), any(UpdateProductStockRequest.class)))
          .willThrow(new EntityNotFoundException("Product", nonExistentId));

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/stock", nonExistentId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el stock es inválido")
    void shouldReturn400WhenStockIsInvalid() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();

      // Act & Assert - Enviar request sin stock
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/stock", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el stock es negativo")
    void shouldReturn400WhenStockIsNegative() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();
      UpdateProductStockRequest request = new UpdateProductStockRequest(-10);

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/stock", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe permitir actualizar stock a cero")
    void shouldAllowUpdatingStockToZero() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      Integer newStock = 0;

      UpdateProductStockRequest request = new UpdateProductStockRequest(newStock);

      ProductResponse response = new ProductResponse(productId, "Hamburguesa", newStock, branchId);

      given(updateProductStockUseCase.execute(eq(productId), any(UpdateProductStockRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/stock", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.stock").value(0));
    }

    @Test
    @DisplayName("Debe manejar UUID inválido correctamente")
    void shouldHandleInvalidUUIDCorrectly() throws Exception {
      // Arrange
      UpdateProductStockRequest request = new UpdateProductStockRequest(100);

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/stock", "invalid-uuid")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/products/{productId}/name - Actualizar Nombre")
  class UpdateProductNameTests {

    @Test
    @DisplayName("Debe actualizar nombre exitosamente y retornar 200")
    void shouldUpdateProductNameSuccessfully() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID branchId = UUID.randomUUID();
      String newName = "Pizza Especial";

      UpdateProductNameRequest request = new UpdateProductNameRequest(newName);

      ProductResponse response = new ProductResponse(productId, newName, 50, branchId);

      given(updateProductNameUseCase.execute(eq(productId), any(UpdateProductNameRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/name", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(productId.toString()))
          .andExpect(jsonPath("$.name").value(newName));

      verify(updateProductNameUseCase).execute(eq(productId), any(UpdateProductNameRequest.class));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el producto no existe")
    void shouldReturn404WhenProductNotFound() throws Exception {
      // Arrange
      UUID nonExistentId = UUID.randomUUID();
      UpdateProductNameRequest request = new UpdateProductNameRequest("Nuevo Nombre");

      given(
              updateProductNameUseCase.execute(
                  eq(nonExistentId), any(UpdateProductNameRequest.class)))
          .willThrow(new EntityNotFoundException("Product", nonExistentId));

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/name", nonExistentId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el nombre es inválido")
    void shouldReturn400WhenNameIsInvalid() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();

      // Act & Assert - Enviar request sin nombre
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/name", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el nombre está vacío")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
      // Arrange
      UUID productId = UUID.randomUUID();
      UpdateProductNameRequest request = new UpdateProductNameRequest("");

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/name", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe manejar UUID inválido correctamente")
    void shouldHandleInvalidUUIDCorrectly() throws Exception {
      // Arrange
      UpdateProductNameRequest request = new UpdateProductNameRequest("Nuevo Nombre");

      // Act & Assert
      mockMvc
          .perform(
              patch("/api/v1/products/{productId}/name", "invalid-uuid")
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
      UUID branchId = UUID.randomUUID();
      CreateProductRequest request = new CreateProductRequest(branchId, "Test", 10);

      // Act & Assert
      mockMvc
          .perform(post("/api/v1/products").content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe aceptar solo application/json como Content-Type")
    void shouldAcceptOnlyApplicationJsonContentType() throws Exception {
      // Arrange
      UUID branchId = UUID.randomUUID();
      String productName = "Test Product";
      Integer stock = 10;
      CreateProductRequest request = new CreateProductRequest(branchId, productName, stock);

      UUID productId = UUID.randomUUID();
      ProductResponse response = new ProductResponse(productId, productName, stock, branchId);

      given(addProductToBranchUseCase.execute(any(CreateProductRequest.class)))
          .willReturn(response);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());
    }
  }
}
