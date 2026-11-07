<div align="center">

# HadakaElectro Backend

### A modular Spring Boot commerce platform for customers, operations, and sales intelligence

<p>
  <a href="https://github.com/ahmed-hadaka/HadakaElectroBackend">
    <img src="https://img.shields.io/github/stars/ahmed-hadaka/HadakaElectroBackend?style=for-the-badge&logo=github&label=Stars" alt="GitHub stars">
  </a>
  <img src="https://img.shields.io/badge/Java-26-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 26">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 4.1">
  <img src="https://img.shields.io/badge/MySQL-Relational%20Persistence-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
</p>

<p>
  <a href="https://github.com/ahmed-hadaka/HadakaElectroFrontEnd">Frontend application</a>
  ·
  <a href="#api-surface">API surface</a>
  ·
  <a href="#running-locally">Run locally</a>
</p>

</div>

## What this project is

HadakaElectro Backend powers an end-to-end electronics commerce experience. It is designed as a multi-module Spring Boot system with separate application boundaries for the public customer experience and the internal operations portal, while keeping shared entities, DTOs, and exception handling in one reusable module.

The result is a portfolio-scale backend that demonstrates more than CRUD: authentication, authorization, catalog management, shopping carts, checkout, order fulfillment, shipping configuration, file exports, and business reporting are modeled as a cohesive product.

## Architecture

```text
                         ┌─────────────────────────────┐
                         │  HadakaElectro Angular app   │
                         └──────────────┬──────────────┘
                                        │
                ┌───────────────────────┴───────────────────────┐
                │                                               │
     ┌──────────▼──────────┐                         ┌──────────▼──────────┐
     │  ElectroCustomerApi  │                         │  ElectroInternalApi  │
     │  :8081 /ElectroCustomer│                       │  :8080 /ElectroInternal│
     └──────────┬──────────┘                         └──────────┬──────────┘
                │                                               │
                └───────────────────────┬───────────────────────┘
                                        │
                         ┌──────────────▼──────────────┐
                         │        ElectroCommon         │
                         │ Entities · DTOs · Exceptions │
                         └──────────────┬──────────────┘
                                        │
                              ┌─────────▼─────────┐
                              │       MySQL        │
                              └────────────────────┘
```

### Modules

| Module | Responsibility | Default base URL |
| --- | --- | --- |
| `ElectroCustomerApi` | Customer authentication, browsing, cart, checkout, addresses, orders, and returns | `http://localhost:8081/ElectroCustomer` |
| `ElectroInternalApi` | Admin and operations workflows for users, products, brands, categories, orders, shipping, settings, and reports | `http://localhost:8080/ElectroInternal` |
| `ElectroCommon` | Shared JPA entities, product/order models, DTOs, validation, and domain exceptions | Library module |

## Product capabilities

### Customer commerce

- Browse categories, search products, and view product details
- Register, verify accounts, reset passwords, and sign in with Google
- Manage a shopping cart and check shipping availability
- Maintain an address book and complete checkout
- Track orders and request returns

### Internal operations

- Role-aware access for `Admin`, `Editor`, `Salesperson`, `Shipper`, and `Assistant`
- Manage products, product images, brands, categories, customers, and users
- Configure countries, states, shipping rates, general settings, and payment settings
- Review and update orders throughout the fulfillment workflow
- Export category data to CSV and user data to PDF
- Analyze sales by date, category, and product

### Engineering highlights

- Spring Security-backed authentication for both application surfaces
- CSRF and session-aware integration with the frontend
- JPA repositories and shared audited domain entities
- Multipart file handling for catalog and user media
- Dedicated service, mapper, repository, DTO, and controller layers
- Repository and service tests using H2 for isolated test execution

## API surface

The APIs are grouped by business capability rather than exposing one oversized controller:

```text
Customer API    /auth       /products       /cart
                /customers  /address-book   /checkout
                /orders     /settings

Internal API    /auth       /users          /products
                /categories /brands         /customers
                /orders     /shipping-rates /settings
                /reports
```

## Running locally

### Prerequisites

- Java 26
- Maven 3.9+
- MySQL 8+
- A database created for the application

### 1. Configure the environment

The two application modules read their runtime configuration from environment variables. Configure the datasource, remember-me key, media directories, and (for the customer API) frontend base URL and Google client ID before starting the services.

Typical variables include:

```bash
export DATASOURCE_URL="jdbc:mysql://localhost:3306/hadaka_electro"
export DATASOURCE_USERNAME="root"
export DATASOURCE_PASSWORD="change-me"
export REMEMBER_ME_KEY="use-a-long-random-value"
export CATEGORY_IMAGES_PATH="/absolute/path/to/category-images"
export BRAND_LOGOS_PATH="/absolute/path/to/brand-logos"
export PRODUCT_IMAGES_PATH="/absolute/path/to/product-images"
export DEFAULT_IMAGES_PATH="/absolute/path/to/default-images"
export USER_PHOTOS_PATH="/absolute/path/to/user-photos"
export FRONTEND_BASE_URL="http://localhost:4200"
export GOOGLE_CLIENT_ID="your-google-client-id"
```

Use local, untracked environment configuration only. Never commit credentials or production keys.

### 2. Build and test all modules

```bash
mvn clean verify
```

### 3. Start an application

Run each command from the repository root in a separate terminal:

```bash
mvn -pl ElectroCustomerApi -am spring-boot:run
mvn -pl ElectroInternalApi -am spring-boot:run
```

The customer API listens on port `8081`; the internal API listens on port `8080`.

## Repository layout

```text
.
├── ElectroCommon/
│   └── src/main/java/.../entities, exception
├── ElectroCustomerApi/
│   ├── src/main/java/.../customer
│   └── src/test/
├── ElectroInternalApi/
│   ├── src/main/java/.../internal
│   └── src/test/
└── pom.xml
```

## Testing

The project includes application-context, repository, service, and security tests across both APIs:

```bash
mvn test
```

## Frontend companion

The Angular client lives in [HadakaElectroFrontEnd](https://github.com/ahmed-hadaka/HadakaElectroFrontEnd). Start the backend APIs first, then configure the frontend environment URLs to point to the two local context paths.

## Why this repository stands out

HadakaElectro demonstrates the engineering decisions expected in a real commerce system: bounded application responsibilities, reusable domain modeling, protected workflows, operational tooling, and reporting—not just a storefront backed by a few endpoints.

## License

This project is maintained as a portfolio and learning project by [Ahmed Hadaka](https://github.com/ahmed-hadaka).
