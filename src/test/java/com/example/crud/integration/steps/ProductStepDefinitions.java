package com.example.crud.integration.steps;

import java.util.List;
import java.util.Map;

import com.example.crud.model.Product;
import com.example.crud.repository.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository repository;

    private ResponseEntity<String> response;
    private Long createdProductId;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @Given("the product database is empty")
    public void theProductDatabaseIsEmpty() {
        repository.deleteAll();
        assertTrue(repository.findAll().isEmpty());
    }

    @Given("the following products exist:")
    public void theFollowingProductsExist(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Product product = new Product(
                    row.get("name"),
                    row.get("description"),
                    Double.parseDouble(row.get("price"))
            );
            repository.save(product);
        }
    }

    @Given("a product with name {string} description {string} and price {double} exists")
    public void aProductExists(String name, String description, double price) {
        Product product = new Product(name, description, price);
        Product saved = repository.save(product);
        createdProductId = saved.getId();
    }

    @When("I create a product with name {string} description {string} and price {double}")
    public void iCreateAProduct(String name, String description, double price) {
        Product product = new Product(name, description, price);
        response = restTemplate.postForEntity("/api/products", product, String.class);
        if (response.getStatusCode().value() == 201 && response.getBody() != null) {
            try {
                String body = response.getBody();
                String idStr = body.split("\"id\":")[1].split("[,}]")[0].trim();
                createdProductId = Long.parseLong(idStr);
            } catch (Exception ignored) {
            }
        }
    }

    @When("I request all products")
    public void iRequestAllProducts() {
        response = restTemplate.getForEntity("/api/products", String.class);
    }

    @When("I request the product by its ID")
    public void iRequestTheProductById() {
        response = restTemplate.getForEntity("/api/products/" + createdProductId, String.class);
    }

    @When("I request a product with ID {long}")
    public void iRequestAProductWithId(long id) {
        response = restTemplate.getForEntity("/api/products/" + id, String.class);
    }

    @When("I update the product name to {string} and price to {double}")
    public void iUpdateTheProduct(String name, double price) {
        Product updated = new Product(name, "Updated description", price);
        response = restTemplate.exchange(
                "/api/products/" + createdProductId,
                HttpMethod.PUT,
                new HttpEntity<>(updated),
                String.class
        );
    }

    @When("I delete the product")
    public void iDeleteTheProduct() {
        response = restTemplate.exchange(
                "/api/products/" + createdProductId,
                HttpMethod.DELETE,
                null,
                String.class
        );
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode().value());
    }

    @And("the response should contain product name {string}")
    public void theResponseShouldContainProductName(String name) {
        assertTrue(response.getBody().contains(name));
    }

    @And("the response should contain {int} products")
    public void theResponseShouldContainProducts(int count) {
        String body = response.getBody();
        long commaCount = body.chars().filter(ch -> ch == '{').count();
        assertEquals(count, commaCount);
    }

    @And("the product should no longer exist")
    public void theProductShouldNoLongerExist() {
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/api/products/" + createdProductId, String.class);
        assertEquals(404, getResponse.getStatusCode().value());
    }
}
