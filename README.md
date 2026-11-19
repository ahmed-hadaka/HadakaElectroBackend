
**Author:** [Ahmed Hadaka](https://www.linkedin.com/in/ahmed-hadaka-8a2124205/)  
**Live Application:** [Hadaka Electro (Vercel)](https://hadaka-electro-front-end-blond.vercel.app/)  

## 📖 Overview
The **ElectroInternal API** is the administrative powerhouse behind the Hadaka Electro e-commerce platform. Built with **Spring Boot** and **Hibernate**, this module provides a secure, role-based backend for store administrators, salespersons, and content editors to manage the entire platform's lifecycle—from product cataloging and cloud file handling to sales reporting and user management.

By entirely decoupling the internal management operations from the customer-facing storefront, this architecture ensures high security, easier scalability, and clean domain boundaries.

## 🛠️ Tech Stack & Architecture
* **Core:** Java 21, Spring Boot, Spring MVC
* **Security:** Spring Security (Session-based, Cross-Domain CORS/CSRF protection, RBAC)
* **Data Access:** Spring Data JPA, Hibernate, MySQL (Local & JawsDB on Heroku)
* **Cloud & Storage:** AWS S3 (AWS SDK v2), Heroku (Multi-Module Monorepo Deployment), Vercel (Angular Frontend)
* **Build Tool:** Maven
* **Architecture:** Multi-module Maven project, DTO Pattern, Service Layer Pattern

### System Architecture Diagram
<pre>
+-----------------------------------------------------------------------+
|                    HadakaElectroBackEnd (Root Maven)                  |
|          Procfile: java -Dserver.port=$PORT -jar $PATH_TO_JAR         |
+-----------------------------------------------------------------------+
             |                                       |
             v                                       v
+-------------------------+             +-------------------------------+
|   ElectroCustomerApi    |             |      ElectroInternalApi       |
|   (Heroku Dyno 1)       |             |      (Heroku Dyno 2)          |
|-------------------------|             |-------------------------------|
| - WebSecurityConfigs    |             | - WebSecurityConfigs (RBAC)   |
| - REST Controllers      |             | - REST Controllers            |
| - Business Services     |             | - Business Services & Reports |
| - CheckEmailUniqueness  |             | - CheckObjectUniqueness       |
| - MailUtility & Checkout|             | - CSV / Excel / PDF Exporters |
| - Gmail SMTP (@Async)   |             | - AmazonS3Util (AWS SDK v2)   |
| - Spring Data JPA Repos |             | - Spring Data JPA Repos       |
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
        |   MySQL Database   |       |    AWS S3 Object Storage      |
        | (JawsDB on Heroku  |       | (brand_logos, category_images,|
        |   / Local MySQL)   |       |  product_images, user_photos, |
        +--------------------+       |  default_images)              |
                                     +-------------------------------+
</pre>

### Entity Relationship Diagram

<img width="3734" height="7394" alt="Diagram_hadaka_electro_db@localhost" src="https://github.com/user-attachments/assets/bda11b3d-80aa-462e-80a8-99ec1621d7e3" />


### Class Diagram

<img width="20632" height="11046" alt="ElectroInternalAPI_ClassDiagram" src="https://github.com/user-attachments/assets/1636d36e-bceb-46d6-86b0-cd464710e542" />


## 🚀 Key Features & Business Logic
* **Role-Based Access Control (RBAC):** Granular permissions ensuring users only access what their role permits (e.g., `Admin`, `Salesperson`, `Editor`, `Shipper`). Salespersons can update prices, while only Admins can alter global settings.
* **Complex Catalog Management:** Full CRUD operations for Products, Brands, and Categories, including multipart form data handling for **AWS S3** image uploads and automated recursive folder/subfolder cleanup.
* **Dynamic Sales Reporting:** Aggregation of sales data filtered by date ranges, categories, and products to feed dynamic charting on the frontend.
* **Multi-Format Reporting:** Export store entities and records directly into **CSV**, **Excel**, and **PDF** formats.
* **Global Store Settings:** Administrative control over site configurations, supported countries/states, currency formatting, and dynamic shipping rates.
* **Order Fulfillment:** Comprehensive order management allowing staff to update order statuses and process returns.

## 🧠 Technical Highlights for Reviewers
* **Multi-Module Architecture:** Cleanly separated the internal administrative logic from the customer API, sharing only essential entities and utilities via the `ElectroCommon` module.
* **Single-Repo, Multi-Service Deployment (Heroku):** Uses a parameterized root `Procfile` (`web: java -Dserver.port=$PORT -jar $PATH_TO_JAR`) to build once and run `ElectroInternalApi` and `ElectroCustomerApi` as independent Heroku dynos from the same repository.
* **Cloud-Native File Storage (AWS S3):** Migrated static media handling from local disk to **AWS S3** (`AmazonS3Util` via AWS SDK v2), supporting public-read object uploads, single-file deletion, and paginated recursive prefix cleanup (`removeFolder`) for nested product galleries.
* **DTO Pattern Implementation:** Strictly separated entity models from API payloads using Mapper classes, preventing `LazyInitializationException` and infinite recursion during JSON serialization.
* **Advanced Cross-Domain CORS & CSRF Protection:** Implemented a readable `XSRF-TOKEN` cookie and credentialed CORS strategy that seamlessly integrates with the Vercel-hosted Angular frontend across separate domains, securing all state-changing `POST`, `PUT`, `PATCH`, and `DELETE` requests.

## ☁️ Production Cloud Deployment
* **Frontend:** Deployed on **Vercel** (Angular SPA with unified Admin/Customer portal selector).
* **Backend APIs:** Deployed as two separate **Heroku** apps (`ElectroInternalApi` and `ElectroCustomerApi`) from this monorepo using the root `Procfile`.
* **Database:** Hosted on **JawsDB MySQL** (Heroku Add-on).
* **Media Storage:** Hosted on **AWS S3** (`eu-north-1`).

  <img width="1460" height="610" alt="HadakaElectro Architecture Diagram drawio" src="https://github.com/user-attachments/assets/6c17982f-06fb-4ac1-834e-56eead673ec7" />


### Required Production Environment Variables (Heroku Config Vars)
```env
PATH_TO_JAR=ElectroInternalApi/target/ElectroInternalApi-0.0.1-SNAPSHOT.jar
JAWSDB_URL=mysql://<user>:<password>@<host>:3306/<database>
FRONTEND_BASE_URL=https://hadaka-electro-front-end-blond.vercel.app
REMEMBER_ME_KEY=<your_remember_me_secret>
AWS_ACCESS_KEY_ID=<your_aws_access_key>
AWS_SECRET_ACCESS_KEY=<your_aws_secret_key>
AWS_REGION=eu-north-1
AWS_BUCKET_NAME=hadaka-electro-storage
```
*(Plus S3 base URL config vars for `BRAND_LOGOS_PATH`, `CATEGORY_IMAGES_PATH`, `PRODUCT_IMAGES_PATH`, `USER_PHOTOS_PATH`, and `DEFAULT_IMAGES_PATH`.)*

## 💻 Local Setup & Installation

### Prerequisites
* JDK 21 or higher
* Maven 3.6+
* MySQL 8.0+
* AWS S3 Bucket & IAM credentials configured in your environment (or `~/.aws/credentials`)

### Database Configuration
1. Open your MySQL shell or client and create the database:
   ```sql
   CREATE DATABASE hadaka_electro;
   ```
2. In `src/main/resources/application.properties`, update your credentials (or provide them via environment variables):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hadaka_electro
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

### Running the Application
1. Navigate to the root of the project (`HadakaElectroBackEnd`).
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

