# Hadaka Electro - Internal API Documentation

**Last Updated:** September 27, 2026

---

## Table of Contents

1. [Authentication Endpoints](#authentication-endpoints)
2. [Brand Endpoints](#brand-endpoints)
3. [Category Endpoints](#category-endpoints)
4. [Product Endpoints](#product-endpoints)
5. [User Endpoints](#user-endpoints)
6. [Customer Endpoints](#customer-endpoints)
7. [Sittings Endpoints](#sittings-endpoints)
8. [Data Types Reference](#data-types-reference)

---

## Authentication Endpoints

### Base URL: `/auth`

#### 1. Get CSRF Token

- **Endpoint:** `GET /auth/`
- **Description:** Initialize request and retrieve CSRF token for security
- **Authentication:** Not required
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** CsrfToken object containing token information

**Example Response:**

```json
{
  "token": "csrf-token-value",
  "parameterName": "_csrf",
  "headerName": "X-CSRF-TOKEN"
}
```

---

#### 2. User Login

- **Endpoint:** `POST /auth/login`
- **Description:** Authenticate user with email and password
- **Authentication:** Not required
- **Content-Type:** application/json
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "rememberMe": true
  }
  ```
- **Request Fields:**
  | Field | Type | Required | Description |
  |-------|------|----------|-------------|
  | email | String | ✓ | User email address |
  | password | String | ✓ | User password |
  | rememberMe | Boolean | ✓ | Enable persistent login |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "Email": "user@example.com",
        "Role": "[ADMIN, USER]"
      }
      ```

- **Possible Exceptions:**
    - `BadCredentialsException` - Invalid email or password
    - `AccountStatusException` - Account disabled or locked

---

#### 3. User Logout

- **Endpoint:** `POST /auth/logout`
- **Description:** Logout current user and invalidate session
- **Authentication:** Required (Session)
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "message": "Logged out successfully"
      }
      ```

---

## Brand Endpoints

### Base URL: `/brands`

#### 1. List All Brands

- **Endpoint:** `GET /brands` or `GET /brands/`
- **Description:** Retrieve paginated list of all brands with optional keyword search
- **Authentication:** Required
- **Request Parameters:**
  | Parameter | Type | Required | Default | Description |
  |-----------|------|----------|---------|-------------|
  | keyword | String | ✗ | null | Search keyword for brand name |
  | page | Integer | ✗ | 0 | Page number (0-indexed) |
  | size | Integer | ✗ | 10 | Number of items per page |
  | sort | String | ✗ | id | Sort field |
  | direction | String | ✗ | ASC | Sort direction (ASC/DESC) |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Page<BrandDTO> object containing:
      ```json
      {
        "content": [
          {
            "id": 1,
            "name": "Samsung",
            "logo": "samsung.png",
            "categories": []
          }
        ],
        "pageable": {
          "pageNumber": 0,
          "pageSize": 10,
          "sort": "id"
        },
        "totalElements": 1,
        "totalPages": 1
      }
      ```

---

#### 2. Get New Brand Form Data

- **Endpoint:** `GET /brands/new-brand`
- **Description:** Retrieve categories for brand creation form
- **Authentication:** Required
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Array of CategorySelectDTO
      ```json
      [
        {
          "id": 1,
          "name": "Electronics"
        }
      ]
      ```

---

#### 3. Save/Create Brand

- **Endpoint:** `POST /brands/save-brand`
- **Description:** Create or update a brand with logo image
- **Authentication:** Required
- **Content-Type:** multipart/form-data
- **Request Parts:**
  | Part | Type | Required | Description |
  |------|------|----------|-------------|
  | brand | BrandDTO | ✓ | Brand data (JSON) |
  | imageFile | File | ✓ | Brand logo image |

- **BrandDTO Structure:**
  ```json
  {
    "id": null,
    "name": "Sony",
    "logo": "sony.png",
    "categories": []
  }
  ```

- **Response:**
    - **Status:** 201 Created
    - **Content-Type:** text/plain
    - **Body:** "Brand with id 5 Saved Successfully."

- **Possible Exceptions:**
    - `DuplicatedObjectException` - Brand name already exists
    - `ObjectNotFoundException` - Category not found

---

#### 4. Edit Brand

- **Endpoint:** `GET /brands/edit/{id}`
- **Description:** Retrieve brand data and categories for editing
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Brand ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "brand": {
          "id": 1,
          "name": "Samsung",
          "logo": "samsung.png",
          "categories": []
        },
        "categories": [
          {
            "id": 1,
            "name": "Electronics"
          }
        ]
      }
      ```

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Brand not found

---

#### 5. Delete Brand

- **Endpoint:** `GET /brands/delete/{id}`
- **Description:** Delete a brand by ID
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Brand ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Brand with id: 1 has been deleted."

- **Possible Exceptions:**
    - `Exception` - General error during deletion

---

## Category Endpoints

### Base URL: `/categories`

#### 1. List All Categories

- **Endpoint:** `GET /categories` or `GET /categories/`
- **Description:** Retrieve paginated list of all categories with optional keyword search
- **Authentication:** Required
- **Request Parameters:**
  | Parameter | Type | Required | Default | Description |
  |-----------|------|----------|---------|-------------|
  | keyword | String | ✗ | null | Search keyword for category name |
  | page | Integer | ✗ | 0 | Page number (0-indexed) |
  | size | Integer | ✗ | 10 | Number of items per page |
  | sort | String | ✗ | id | Sort field |
  | direction | String | ✗ | ASC | Sort direction (ASC/DESC) |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Page<CategoryListDTO>
      ```json
      {
        "content": [
          {
            "id": 1,
            "name": "Electronics",
            "alias": "electronics",
            "image": "electronics.png",
            "parent": null,
            "enabled": true
          }
        ],
        "totalElements": 1,
        "totalPages": 1
      }
      ```

---

#### 2. Get New Category Form Data

- **Endpoint:** `GET /categories/new-category`
- **Description:** Retrieve all categories for parent category selection in creation form
- **Authentication:** Required
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Array of CategorySelectDTO

---

#### 3. Save/Create Category

- **Endpoint:** `POST /categories/save-category`
- **Description:** Create or update a category with image
- **Authentication:** Required
- **Content-Type:** multipart/form-data
- **Request Parts:**
  | Part | Type | Required | Description |
  |------|------|----------|-------------|
  | category | CategoryListDTO | ✓ | Category data (JSON) |
  | imageFile | File | ✓ | Category image |

- **CategoryListDTO Structure:**
  ```json
  {
    "id": null,
    "name": "Laptops",
    "alias": "laptops",
    "image": "laptops.png",
    "parent": null,
    "enabled": true
  }
  ```

- **Response:**
    - **Status:** 201 Created
    - **Content-Type:** text/plain
    - **Body:** "Category Saved Successfully."

- **Possible Exceptions:**
    - `DuplicatedObjectException` - Category name already exists
    - `IOException` - File upload error

---

#### 4. Edit Category

- **Endpoint:** `GET /categories/edit/{id}`
- **Description:** Retrieve category data and available parent categories for editing
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Category ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "category": {
          "id": 1,
          "name": "Electronics",
          "alias": "electronics",
          "image": "electronics.png",
          "parent": null,
          "enabled": true
        },
        "categories": [
          {
            "id": 2,
            "name": "Computers"
          }
        ]
      }
      ```

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Category not found

---

#### 5. Delete Category

- **Endpoint:** `GET /categories/delete/{id}`
- **Description:** Delete a category by ID
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Category ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Category with id: 1 has been deleted."

---

#### 6. Update Category Enable Status

- **Endpoint:** `GET /categories/updateEnableStatus/{id}`
- **Description:** Toggle enable/disable status of a category
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Category ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Category id: 1 has been enabled successfully."

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Category not found

---

#### 7. Export Categories to CSV

- **Endpoint:** `GET /categories/export/csv`
- **Description:** Export all categories to CSV file
- **Authentication:** Required
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/csv
    - **Body:** CSV file download (filename: categories.csv)

---

## Product Endpoints

### Base URL: `/products`

#### 1. List All Products

- **Endpoint:** `GET /products` or `GET /products/`
- **Description:** Retrieve paginated list of all products with optional filtering and search
- **Authentication:** Required
- **Request Parameters:**
  | Parameter | Type | Required | Default | Description |
  |-----------|------|----------|---------|-------------|
  | keyword | String | ✗ | null | Search keyword for product name |
  | categoryId | Integer | ✗ | null | Filter by category ID |
  | page | Integer | ✗ | 0 | Page number (0-indexed) |
  | size | Integer | ✗ | 7 | Number of items per page |
  | sort | String | ✗ | id | Sort field |
  | direction | String | ✗ | ASC | Sort direction (ASC/DESC) |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "products": {
          "content": [
            {
              "id": 1,
              "name": "Dell Laptop",
              "categoryId": 2,
              "mainImage": "dell-laptop.jpg",
              "brandId": 1,
              "enabled": true
            }
          ],
          "totalElements": 1,
          "totalPages": 1
        },
        "categories": [
          {
            "id": 1,
            "name": "Electronics"
          }
        ]
      }
      ```

---

#### 2. Get New Product Form Data

- **Endpoint:** `GET /products/new-product`
- **Description:** Retrieve brands list for product creation form
- **Authentication:** Required
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "brands": [
          {
            "id": 1,
            "name": "Dell"
          }
        ]
      }
      ```

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Brands not found

---

#### 3. Save/Create Product

- **Endpoint:** `POST /products/save-product`
- **Description:** Create or update a product with images. Salesperson can only update prices.
- **Authentication:** Required (Admin or Salesperson)
- **Content-Type:** multipart/form-data
- **Request Parts:**
  | Part | Type | Required | Description |
  |------|------|----------|-------------|
  | product | ProductDTO | ✓ | Product data (JSON) |
  | main_image | File | ✗ | Main product image |
  | extra_images | File[] | ✗ | Additional product images |

- **ProductDTO Structure:**
  ```json
  {
    "id": null,
    "name": "Dell XPS 13",
    "alias": "dell-xps-13",
    "categoryId": 2,
    "brandId": 1,
    "enabled": true,
    "inStock": true,
    "cost": 500.00,
    "price": 999.99,
    "discountPercent": 10.0,
    "shortDescription": "Ultra-portable laptop with great performance",
    "fullDescription": "The Dell XPS 13 is a premium ultrabook designed for professionals...",
    "mainImage": "xps13.jpg",
    "productImages": [],
    "productDetails": [],
    "length": 30.2,
    "width": 20.0,
    "height": 1.7,
    "weight": 1.2
  }
  ```

- **Response:**
    - **Status:** 201 Created
    - **Content-Type:** application/json
    - **Body:** "Product with ID 5 saved successfully."

- **Possible Exceptions:**
    - `DuplicatedObjectException` - Product already exists
    - `ObjectNotFoundException` - Brand or category not found
    - `IOException` - File upload error

---

#### 4. View Product Details

- **Endpoint:** `GET /products/{id}`
- **Description:** Retrieve detailed information for a specific product
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Product ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** ProductDTO object with all details

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Product not found

---

#### 5. Edit Product

- **Endpoint:** `GET /products/edit/{id}`
- **Description:** Retrieve product data and brands for editing
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Product ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "product": { /* ProductDTO */ },
        "brands": [
          {
            "id": 1,
            "name": "Dell"
          }
        ]
      }
      ```

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Product not found

---

#### 6. Update Product Enable Status

- **Endpoint:** `PUT /products/{id}/enabled/{status}`
- **Description:** Enable or disable a product
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Product ID |
  | status | Boolean | ✓ | Desired status (true=disable, false=enable) |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Product with ID 1 has been Disabled."

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Product not found

---

#### 7. Delete Product

- **Endpoint:** `GET /products/delete/{id}`
- **Description:** Delete a product by ID
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | Product ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Product with ID 1 has been deleted."

- **Possible Exceptions:**
    - `Exception` - General error during deletion

---

## User Endpoints

### Base URL: `/users`

#### 1. List All Users

- **Endpoint:** `GET /users` or `GET /users/`
- **Description:** Retrieve paginated list of all users with optional keyword search
- **Authentication:** Required
- **Request Parameters:**
  | Parameter | Type | Required | Default | Description |
  |-----------|------|----------|---------|-------------|
  | keyword | String | ✗ | null | Search keyword for user email or name |
  | page | Integer | ✗ | 0 | Page number (0-indexed) |
  | size | Integer | ✗ | 10 | Number of items per page |
  | sort | String | ✗ | id | Sort field |
  | direction | String | ✗ | ASC | Sort direction (ASC/DESC) |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Page<UserDTO>
      ```json
      {
        "content": [
          {
            "id": 1,
            "email": "admin@example.com",
            "firstName": "John",
            "lastName": "Doe",
            "enabled": true,
            "password": null,
            "photo": "john.jpg",
            "roles": [
              {
                "id": 1,
                "name": "ADMIN"
              }
            ]
          }
        ],
        "totalElements": 1,
        "totalPages": 1
      }
      ```

---

#### 2. Get Current User ID

- **Endpoint:** `GET /users/me`
- **Description:** Retrieve ID of the currently authenticated user
- **Authentication:** Required (Session)
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Integer (User ID)
      ```json
      1
      ```

---

#### 3. Save/Create User

- **Endpoint:** `POST /users/saveUser`
- **Description:** Create or update a user with profile photo
- **Authentication:** Required
- **Content-Type:** multipart/form-data
- **Request Parts:**
  | Part | Type | Required | Description |
  |------|------|----------|-------------|
  | user | UserDTO | ✓ | User data (JSON) |
  | photo | File | ✓ | User profile photo |

- **UserDTO Structure:**
  ```json
  {
    "id": null,
    "email": "newuser@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "enabled": true,
    "password": "securePassword123",
    "photo": "jane.jpg",
    "roles": [
      {
        "id": 2,
        "name": "SALESPERSON"
      }
    ]
  }
  ```

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "message": "User saved successfully."
      }
      ```

- **Possible Exceptions:**
    - `DuplicatedObjectException` - Email already exists
    - `IOException` - File upload error

---

#### 4. Get User by ID

- **Endpoint:** `GET /users/{id}`
- **Description:** Retrieve detailed information for a specific user
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | User ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** UserDTO object

---

#### 5. Delete User

- **Endpoint:** `DELETE /users/delete/{id}`
- **Description:** Delete a user by ID
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | User ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "message": "User with id 1 deleted successfully"
      }
      ```

- **Possible Exceptions:**
    - `Exception` - General error during deletion

---

#### 6. Update User Enable Status

- **Endpoint:** `GET /users/updateEnableStatus/{id}`
- **Description:** Toggle enable/disable status of a user
- **Authentication:** Required
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Integer | ✓ | User ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "User with id 1 has been enabled successfully"

- **Possible Exceptions:**
    - `ObjectNotFoundException` - User not found

---

#### 7. Export Users to PDF

- **Endpoint:** `GET /users/export/pdf`
- **Description:** Export all users to PDF file
- **Authentication:** Required
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/pdf
    - **Body:** PDF file download (filename: Hadaka_Electro_Users.pdf)

---

## Customer Endpoints

### Base URL: `/customers`

#### 1. List All Customers

- **Endpoint:** `GET /customers` or `GET /customers/`
- **Description:** Retrieve paginated list of all customers with optional keyword search
- **Authentication:** Required (Admin or Salesperson)
- **Request Parameters:**
  | Parameter | Type | Required | Default | Description |
  |-----------|------|----------|---------|-------------|
  | keyword | String | ✗ | null | Search keyword across customer profile fields |
  | page | Integer | ✗ | 0 | Page number (0-indexed) |
  | size | Integer | ✗ | 20 | Number of items per page (Spring default) |
  | sort | String | ✗ | firstName | Sort field |
  | direction | String | ✗ | ASC | Sort direction (ASC/DESC) |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Page<CustomerDTO>
      ```json
      {
        "content": [
          {
            "id": 1,
            "email": "customer@example.com",
            "password": null,
            "firstName": "John",
            "lastName": "Doe",
            "phoneNumber": "+201234567890",
            "addressLine1": "123 Main Street",
            "addressLine2": "Apartment 4",
            "city": "Cairo",
            "state": "Cairo",
            "postalCode": "11765",
            "countryId": 1
          }
        ],
        "totalElements": 1,
        "totalPages": 1
      }
      ```

---

#### 2. Get Customer by ID

- **Endpoint:** `GET /customers/{id}`
- **Description:** Retrieve detailed information for a specific customer
- **Authentication:** Required (Admin or Salesperson)
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Long | ✓ | Customer ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** CustomerDTO object

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Customer not found

---

#### 3. Update Customer

- **Endpoint:** `POST /customers/update`
- **Description:** Update an existing customer profile
- **Authentication:** Required (Admin or Salesperson)
- **Content-Type:** application/json
- **Request Body:** CustomerDTO
  ```json
  {
    "id": 1,
    "email": "customer@example.com",
    "password": "newPassword123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+201234567890",
    "addressLine1": "123 Main Street",
    "addressLine2": "Apartment 4",
    "city": "Cairo",
    "state": "Cairo",
    "postalCode": "11765",
    "countryId": 1
  }
  ```

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Customer Id: 1 updated successfully"

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Customer or country not found

---

#### 4. Toggle Customer Enable Status

- **Endpoint:** `POST /customers/toggle-enable-status/{id}`
- **Description:** Toggle enable/disable status of a customer
- **Authentication:** Required (Admin or Salesperson)
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Long | ✓ | Customer ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Customer id: 1 has been Enabled successfully."

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Customer not found

---

#### 5. Delete Customer

- **Endpoint:** `DELETE /customers/delete/{id}`
- **Description:** Delete a customer by ID
- **Authentication:** Required (Admin or Salesperson)
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | id | Long | ✓ | Customer ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Customer id: 1 has been deleted successfully."

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Customer not found

---

## Sittings Endpoints

### Base URL: `/sittings`

#### 1. Get General and Currency Sittings

- **Endpoint:** `GET /sittings/general`
- **Description:** Retrieve general settings and available currencies
- **Authentication:** Required (Admin)
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:**
      ```json
      {
        "generalSettings": [
          {
            "key": "SITE_NAME",
            "value": "Hadaka Electro",
            "category": "GENERAL"
          }
        ],
        "currencyList": [
          {
            "id": 1,
            "name": "US Dollar",
            "symbol": "$",
            "code": "USD"
          }
        ]
      }
      ```

---

#### 2. Update General Sittings

- **Endpoint:** `POST /sittings/update-general`
- **Description:** Update general settings and optionally upload a site logo
- **Authentication:** Required (Admin)
- **Content-Type:** multipart/form-data
- **Request Parts:**
  | Part | Type | Required | Description |
  |------|------|----------|-------------|
  | updated-sittings | Array<Sitting> | ✓ | List of sitting key/value updates |
  | site-logo | File | ✗ | Site logo image file |

- **Sitting Structure:**
  ```json
  {
    "key": "SITE_NAME",
    "value": "Hadaka Electro",
    "category": "GENERAL"
  }
  ```

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "General Sittings Updated Successfully"

- **Possible Exceptions:**
    - `FileStorageException` - Site logo storage failed

---

#### 3. List Countries

- **Endpoint:** `GET /sittings/countries`
- **Description:** Retrieve all countries
- **Authentication:** Required (Admin)
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Array of CountryDTO
      ```json
      [
        {
          "id": 1,
          "name": "Egypt",
          "code": "EG"
        }
      ]
      ```

---

#### 4. Save Country

- **Endpoint:** `POST /sittings/save-country`
- **Description:** Create or update a country
- **Authentication:** Required (Admin)
- **Content-Type:** application/json
- **Request Body:** CountryDTO
  ```json
  {
    "id": null,
    "name": "Egypt",
    "code": "EG"
  }
  ```

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Country : Egypt Saved Successfully"

---

#### 5. Delete Country

- **Endpoint:** `DELETE /sittings/delete-country/{country_id}`
- **Description:** Delete country by ID
- **Authentication:** Required (Admin)
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | country_id | Long | ✓ | Country ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "Country with id: 1 Deleted Successfully"

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Country not found

---

#### 6. List States by Country

- **Endpoint:** `GET /sittings/states/{country_id}`
- **Description:** Retrieve states for a specific country
- **Authentication:** Required (Admin)
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | country_id | Long | ✓ | Country ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Array of StateDTO
      ```json
      [
        {
          "id": 1,
          "name": "Cairo",
          "countryId": 1
        }
      ]
      ```

---

#### 7. Save State

- **Endpoint:** `POST /sittings/save-state`
- **Description:** Create or update a state
- **Authentication:** Required (Admin)
- **Content-Type:** application/json
- **Request Body:** StateDTO
  ```json
  {
    "id": null,
    "name": "Cairo",
    "countryId": 1
  }
  ```

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "State: Cairo Saved Successfully"

- **Possible Exceptions:**
    - `ObjectNotFoundException` - Country not found

---

#### 8. Delete State

- **Endpoint:** `DELETE /sittings/delete-state/{state_id}`
- **Description:** Delete state by ID
- **Authentication:** Required (Admin)
- **Path Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | state_id | Long | ✓ | State ID |

- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** text/plain
    - **Body:** "State with id: 1 Deleted Successfully"

- **Possible Exceptions:**
    - `ObjectNotFoundException` - State or country not found

---

#### 9. Get Mail Server Sittings

- **Endpoint:** `GET /sittings/mail-server`
- **Description:** Retrieve mail server settings
- **Authentication:** Required (Admin)
- **Request Parameters:** None
- **Response:**
    - **Status:** 200 OK
    - **Content-Type:** application/json
    - **Body:** Array<Sitting> where category is `MAIL_SERVER`
      ```json
      [
        {
          "key": "MAIL_HOST",
          "value": "smtp.gmail.com",
          "category": "MAIL_SERVER"
        },
        {
          "key": "MAIL_PORT",
          "value": "587",
          "category": "MAIL_SERVER"
        }
      ]
      ```

---

## Data Types Reference

### BrandDTO

```json
{
  "id": "Integer",
  "name": "String (4-∞ chars)",
  "logo": "String",
  "categories": "Set<CategorySelectDTO>"
}
```

### BrandListDTO

```json
{
  "id": "Integer",
  "name": "String"
}
```

### CategorySelectDTO

```json
{
  "id": "Integer",
  "name": "String"
}
```

### CategoryListDTO

```json
{
  "id": "Integer",
  "name": "String",
  "alias": "String",
  "image": "String",
  "parent": "CategorySelectDTO | null",
  "enabled": "Boolean"
}
```

### ProductDTO

```json
{
  "id": "Integer",
  "name": "String (2-256 chars)",
  "alias": "String (2-256 chars)",
  "categoryId": "Integer",
  "brandId": "Integer",
  "enabled": "Boolean",
  "inStock": "Boolean",
  "cost": "Double",
  "price": "Double",
  "discountPercent": "Double",
  "shortDescription": "String (5+ chars)",
  "fullDescription": "String (10+ chars)",
  "mainImage": "String",
  "productImages": "Set<ProductImagesDTO>",
  "productDetails": "Set<ProductDetailsDTO>",
  "length": "Double",
  "width": "Double",
  "height": "Double",
  "weight": "Double"
}
```

### ProductListDTO

```json
{
  "id": "Integer",
  "name": "String",
  "categoryId": "Integer",
  "mainImage": "String",
  "brandId": "Integer",
  "enabled": "Boolean"
}
```

### UserDTO

```json
{
  "id": "Integer",
  "email": "String (valid email format)",
  "firstName": "String (3+ chars)",
  "lastName": "String (3+ chars)",
  "enabled": "Boolean",
  "password": "String",
  "photo": "String (filename)",
  "roles": "Set<Role>"
}
```

### CustomerDTO

```json
{
  "id": "Long (required)",
  "email": "String (valid email, max 45 chars)",
  "password": "String (8-64 chars, optional on update)",
  "firstName": "String (2-45 chars)",
  "lastName": "String (2-45 chars)",
  "phoneNumber": "String (max 15 chars)",
  "addressLine1": "String (max 64 chars)",
  "addressLine2": "String (max 64 chars, optional)",
  "city": "String (max 45 chars)",
  "state": "String (max 45 chars)",
  "postalCode": "String (max 10 chars)",
  "countryId": "Long (required)"
}
```

### CountryDTO

```json
{
  "id": "Long | null",
  "name": "String (not blank)",
  "code": "String (not blank)"
}
```

### StateDTO

```json
{
  "id": "Long | null",
  "name": "String (not blank)",
  "countryId": "Long (>= 1)"
}
```

### Sitting

```json
{
  "key": "String (max 128 chars)",
  "value": "String (max 1024 chars)",
  "category": "SettingCategory"
}
```

### Currency

```json
{
  "id": "Long",
  "name": "String (max 64 chars)",
  "symbol": "String (max 3 chars)",
  "code": "String (max 4 chars)"
}
```

### SittingCategory

```json
[
  "GENERAL",
  "MAIL_SERVER",
  "MAIL_TEMPLATES",
  "CURRENCY",
  "PAYMENT"
]
```

### LoginRequestDTO

```json
{
  "email": "String",
  "password": "String",
  "rememberMe": "Boolean"
}
```

### CsrfToken (from Spring Security)

```json
{
  "token": "String",
  "parameterName": "String",
  "headerName": "String"
}
```

---

## Common Response Patterns

### Success Response

- **HTTP Status:** 200 OK or 201 Created
- **Content-Type:** application/json
- **Body:** Varies by endpoint

### Error Response

- **HTTP Status:** 4xx or 5xx
- **Content-Type:** application/json
- **Body:** Exception message or error details

### Pagination Response

```json
{
  "content": [ /* Array of items */ ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": "id"
  },
  "totalElements": 100,
  "totalPages": 10
}
```

---

## Authentication & Security

- **Session-based Authentication:** The API uses Spring Security session-based authentication
- **CSRF Protection:** CSRF tokens are required for state-changing operations
- **Remember-Me Cookie:** Optional persistent login via "rememberMe" parameter
- **Role-based Access Control:** Different roles have different permissions
    - **ADMIN:** Full access to all endpoints
    - **SALESPERSON:** Limited access (can only update product prices)

---

## File Upload Specifications

- **Supported Formats:** Common image formats (JPG, PNG, GIF, etc.)
- **Max File Size:** To be determined by server configuration
- **Content-Type:** multipart/form-data

---

## Error Handling

The API uses standard HTTP status codes:

- **200 OK:** Request successful
- **201 Created:** Resource created successfully
- **400 Bad Request:** Invalid request parameters
- **401 Unauthorized:** Authentication required or failed
- **403 Forbidden:** Insufficient permissions
- **404 Not Found:** Resource not found
- **409 Conflict:** Resource already exists (DuplicatedObjectException)
- **500 Internal Server Error:** Server error

---

## Notes for Frontend Integration

1. **Always validate email format** before sending login requests
2. **Include CSRF token** from the `/auth/` endpoint in state-changing requests
3. **Handle pagination** with appropriate page size and sorting
4. **Implement file upload** with progress indication for better UX
5. **Store authentication** state properly after login
6. **Refresh token** periodically to maintain session
7. **Handle 401 responses** by redirecting to login page

---

**API Version:** 1.0  
**Last Updated:** September 27, 2026  
**Developed by:** Hadaka Electro Development Team
