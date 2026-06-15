Feature: Product CRUD Operations
  As an API consumer
  I want to manage products through REST endpoints
  So that I can create, read, update, and delete products

  Scenario: Create a new product
    Given the product database is empty
    When I create a product with name "Laptop" description "A powerful laptop" and price 999.99
    Then the response status should be 201
    And the response should contain product name "Laptop"

  Scenario: Get all products
    Given the following products exist:
      | name   | description     | price  |
      | Laptop | A laptop        | 999.99 |
      | Phone  | A smartphone    | 499.99 |
    When I request all products
    Then the response status should be 200
    And the response should contain 2 products

  Scenario: Get a product by ID
    Given a product with name "Laptop" description "A powerful laptop" and price 999.99 exists
    When I request the product by its ID
    Then the response status should be 200
    And the response should contain product name "Laptop"

  Scenario: Update an existing product
    Given a product with name "Laptop" description "A powerful laptop" and price 999.99 exists
    When I update the product name to "Laptop Pro" and price to 1299.99
    Then the response status should be 200
    And the response should contain product name "Laptop Pro"

  Scenario: Delete a product
    Given a product with name "Laptop" description "A powerful laptop" and price 999.99 exists
    When I delete the product
    Then the response status should be 204
    And the product should no longer exist

  Scenario: Get a non-existing product returns 404
    Given the product database is empty
    When I request a product with ID 999
    Then the response status should be 404
