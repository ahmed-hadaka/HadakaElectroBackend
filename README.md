# ⚙️ Hadaka Electro - Internal Admin API

**Author:** [Ahmed Hadaka](https://www.linkedin.com/in/ahmed-hadaka-8a2124205/)  
**Demo Video:** [[Watch the Local Demo Video Here]](#) *(Link coming soon)*

## 📖 Overview
The **ElectroInternal API** is the administrative powerhouse behind the Hadaka Electro e-commerce platform. Built with **Spring Boot** and **Hibernate**, this module provides a secure, role-based backend for store administrators, salespersons, and content editors to manage the entire platform's lifecycle—from product cataloging and file handling to sales reporting and user management.

By entirely decoupling the internal management operations from the customer-facing storefront, this architecture ensures high security, easier scalability, and clean domain boundaries.

## 🛠️ Tech Stack & Architecture
* **Core:** Java, Spring Boot, Spring MVC
* **Security:** Spring Security (Session-based, CSRF protection, RBAC)
* **Data Access:** Spring Data JPA, Hibernate, MySQL
* **Build Tool:** Maven
* **Architecture:** Multi-module Maven project, DTO Pattern, Service Layer Pattern

### System Architecture Diagram
<pre>
+-----------------------------------------------------------------------+
|                    HadakaElectroBackEnd (Root Maven)                  |
+-----------------------------------------------------------------------+
             |                                       |
             v                                       v
+-------------------------+             +-------------------------------+
|   ElectroCustomerApi    |             |      ElectroInternalApi       |
|-------------------------|             |-------------------------------|
| - WebSecurityConfigs    |             | - WebSecurityConfigs (RBAC)   |
| - REST Controllers      |             | - REST Controllers            |
| - Business Services     |             | - Business Services & Reports |
| - CheckEmailUniqueness  |             | - CheckObjectUniqueness       |
| - MailUtility & Checkout|             | - CSV / PDF Exporters         |
| - Spring Data JPA Repos |             | - Spring Data JPA Repos       |
| - MvcConfigs (Resources)|             | - MvcConfig (Resources)       |
+-------------------------+             +-------------------------------+
             |                                       |
             +-------------------+-------------------+
                                 |
                                 v
             +---------------------------------------+
             |             ElectroCommon             |
             |---------------------------------------|
             | - Shared JPA Entities (Order, Product,|
             |   Customer, User, Setting, Brand...)  |
             | - Shared Product DTOs                 |
             | - GlobalExceptionController & Custom  |
             |   Exceptions                          |
             +---------------------------------------+
                         |               |
                         v               v
        +--------------------+       +-------------------------------+
        |   MySQL Database   |       |   Local Disk Static Storage   |
        | (hadaka_electro_db)|       | (brand_logos, category_images,|
        +--------------------+       |  product_images, default_img) |
                                     +-------------------------------+
</pre>

### Entity Relationship Diagram

<img width="3734" height="7394" alt="Diagram_hadaka_electro_db@localhost" src="https://github.com/user-attachments/assets/bda11b3d-80aa-462e-80a8-99ec1621d7e3" />


### Class Diagram

<img width="20632" height="11046" alt="ElectroInternalAPI_ClassDiagram" src="https://github.com/user-attachments/assets/1636d36e-bceb-46d6-86b0-cd464710e542" />



## 🚀 Key Features & Business Logic
* **Role-Based Access Control (RBAC):** Granular permissions ensuring users only access what their role permits (e.g., `Admin`, `Salesperson`, `Editor`, `Shipper`). Salespersons can update prices, while only Admins can alter global settings.
* **Complex Catalog Management:** Full CRUD operations for Products, Brands, and Categories, including multipart form data handling for image uploads and dynamic directory management.
* **Dynamic Sales Reporting:** Aggregation of sales data filtered by date ranges, categories, and products to feed dynamic charting on the frontend.
* **Global Store Settings:** Administrative control over site configurations, supported countries/states, currency formatting, and dynamic shipping rates.
* **Order Fulfillment:** Comprehensive order management allowing staff to update order statuses and process returns.

## 🧠 Technical Highlights for Reviewers
* **Multi-Module Architecture:** Cleanly separated the internal administrative logic from the customer API, sharing only essential entities and utilities via the `ElectroCommon` module.
* **DTO Pattern implementation:** Strictly separated entity models from API payloads using Mapper classes, preventing `LazyInitializationException` and infinite recursion during JSON serialization.
* **Advanced CSRF Protection:** Implemented a readable `XSRF-TOKEN` cookie strategy that smoothly integrates with Angular frontends, securing all state-changing `POST`, `PUT`, `PATCH`, and `DELETE` requests.
* **Multi-Part File Handling:** Custom internal file storage service to securely write, read, and serve dynamic images (brand logos, product galleries) directly from the server disk.

## 💻 Local Setup & Installation

### Prerequisites
* JDK 17 or higher
* Maven 3.6+
* MySQL 8.0+

### Database Configuration
1. Open your MySQL shell or client and create the database:
   ```sql
   CREATE DATABASE hadaka_electro;
   ```
2. In `src/main/resources/application.properties`, update your credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hadaka_electro
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```


### Running the Application
1. Navigate to the root of the Internal API module.
2. Clean and compile the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
4. The API will be available at `http://localhost:8080/ElectroAdmin` (or the configured context path).

## 📄 API Documentation
For a complete list of endpoints, request payloads, and response structures, please see the [Customer Backend API Documentation](https://github.com/ahmed-hadaka/HadakaElectroBackend/blob/main/ElectroCustomerApi/docs/customer-api-docs.md) & [Internal Backend API Documentation](https://github.com/ahmed-hadaka/HadakaElectroBackend/blob/main/ElectroInternalApi/docs/internal-api-docs.md) included in this repository.
