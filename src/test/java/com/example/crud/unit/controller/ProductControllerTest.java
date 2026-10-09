package com.example.crud.unit.controller;

import java.util.Arrays;

import com.example.crud.controller.ProductController;
import com.example.crud.exception.GlobalExceptionHandler;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.model.Product;
import com.example.crud.service.ProductService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void getAll_ReturnsListOfProducts() throws Exception {
        Product p1 = new Product("Laptop", "A laptop", 999.99);
        p1.setId(1L);
        Product p2 = new Product("Phone", "A phone", 499.99);
        p2.setId(2L);
        when(service.findAll()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by ID")
    void getById_ExistingId_ReturnsProduct() throws Exception {
        Product p = new Product("Laptop", "A laptop", 999.99);
        p.setId(1L);
        when(service.findById(1L)).thenReturn(p);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 for non-existing product")
    void getById_NonExistingId_Returns404() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found with id: 99"));
    }

    @Test
    @DisplayName("POST /api/products - Should create a new product")
    void create_ValidProduct_Returns201() throws Exception {
        Product p = new Product("Laptop", "A laptop", 999.99);
        p.setId(1L);
        when(service.create(any(Product.class))).thenReturn(p);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for invalid product")
    void create_InvalidProduct_Returns400() throws Exception {
        Product invalid = new Product("", "", -10);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update an existing product")
    void update_ExistingProduct_Returns200() throws Exception {
        Product updated = new Product("Laptop Pro", "Upgraded", 1299.99);
        updated.setId(1L);
        when(service.update(eq(1L), any(Product.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete product")
    void delete_ExistingProduct_Returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
