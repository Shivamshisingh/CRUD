package com.example.crud.unit.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.model.Product;
import com.example.crud.repository.ProductRepository;
import com.example.crud.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Laptop", "A powerful laptop", 999.99);
        product.setId(1L);
    }

    @Test
    @DisplayName("Should return all products")
    void findAll_ReturnsListOfProducts() {
        Product product2 = new Product("Phone", "A smartphone", 499.99);
        product2.setId(2L);
        when(repository.findAll()).thenReturn(Arrays.asList(product, product2));

        List<Product> result = service.findAll();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product by ID")
    void findById_ExistingId_ReturnsProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void findById_NonExistingId_ThrowsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    @DisplayName("Should create a new product")
    void create_ValidProduct_ReturnsCreatedProduct() {
        when(repository.save(any(Product.class))).thenReturn(product);

        Product result = service.create(product);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(repository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should update an existing product")
    void update_ExistingProduct_ReturnsUpdatedProduct() {
        Product updated = new Product("Laptop Pro", "An upgraded laptop", 1299.99);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        Product result = service.update(1L, updated);

        assertNotNull(result);
        assertEquals("Laptop Pro", result.getName());
        assertEquals(1299.99, result.getPrice());
        verify(repository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should delete an existing product")
    void delete_ExistingProduct_DeletesSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository, times(1)).delete(product);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing product")
    void delete_NonExistingProduct_ThrowsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}
