# Customer API Docs

Base URL: `/ElectroCustomer`

These endpoints are the ones the Angular frontend should use. The API is session-based, so browser requests must include cookies (`withCredentials: true` in Angular) for login/logout flows.

## Authentication

### POST `/auth/login`

Login with email/password.

**Body**
```json
{
  "email": "user@example.com",
  "password": "secret123",
  "rememberMe": true
}
```

**Response 200**
```json
{
  "Email": "user@example.com",
  "Role": "[ROLE_CUSTOMER]"
}
```

**Notes**
- Creates a server session and may set `JSESSIONID`.
- If `rememberMe` is `true`, a remember-me cookie is also issued.

### POST `/auth/google-login`

Login or auto-register with a Google ID token.

**Body**
```json
{
  "idToken": "google-id-token"
}
```

**Response 200**
```json
{
  "Email": "user@example.com",
  "Role": "[ROLE_CUSTOMER]"
}
```

**Response 401**
```json
{
  "error": "Invalid Google ID token"
}
```

### POST `/auth/logout`

Ends the current session.

**Response 200**
```json
{
  "message": "Logged out successfully"
}
```

## Customer registration

### GET `/customers/register`

Returns the country list used by the registration form.

**Response 200**
```json
[
  {
    "id": 1,
    "name": "United States",
    "code": "US",
    "states": [
      {
        "id": 10,
        "name": "California"
      }
    ]
  }
]
```

### POST `/customers/save-customer`

Creates a new customer and sends a verification email.

**Body**
```json
{
  "id": 0,
  "email": "user@example.com",
  "password": "secret1234",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "1234567890",
  "addressLine1": "Street 1",
  "addressLine2": "",
  "city": "Riyadh",
  "state": "Riyadh",
  "postalCode": "12345",
  "countryId": 1
}
```

**Response 200**
```text
Customer: John Doe Registered successfully
Please check your email to verify your account
```

### GET `/customers/verify?code={verificationCode}`

Verifies the customer email from the link sent in the registration email.

**Response 200**
```json
{
  "message": "Customer Verified Successfully, Now you can login to the website"
}
```

### POST `/customers/request-password-reset?customer-email={email}`

Starts the password reset flow and sends a reset link to the customer email.

**Response 200**
```text
We have sent a reset password link to your email, Please check.
```

**Notes**
- The controller expects `customer-email` as a query parameter.
- The email link points the frontend to `/reset-password?code={token}`.

### POST `/customers/reset-password`

Completes the password reset flow using the reset token from the email link.

**Body**
```json
{
  "resetPasswordToken": "reset-token-from-email-link",
  "password": "newPassword123"
}
```

**Response 200**
```text
You have successfully change your password, Please login.
```

## Home / category listing

### GET `/`
### GET `/list-categories`

Returns enabled leaf categories as a Spring `Page`.

**Query params**
- `page`
- `size`
- `sort`

**Response 200**
```json
{
  "content": [
    {
      "id": 5,
      "name": "Smartphones",
      "alias": "smartphones",
      "image": "/category_images/phones.png",
      "enabled": true
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

## Products

### GET `/products/c/{category-id}`

Returns products for a category plus its parent category trail.

**Query params**
- `page`
- `size`
- `sort`

**Response 200**
```json
{
  "parentCategories": [
    {
      "id": 1,
      "name": "Electronics",
      "alias": "electronics",
      "image": "/category_images/electronics.png",
      "enabled": true
    }
  ],
  "products": {
    "content": [
      {
        "id": 101,
        "name": "iPhone 15",
        "alias": "iphone-15",
        "price": 999.0,
        "priceAfterDiscount": 899.0,
        "mainImage": "/product_images/iphone-15.png"
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### GET `/products/p/{product-id}`

Returns the full product detail plus parent categories.

**Response 200**
```json
{
  "parentCategories": [
    {
      "id": 1,
      "name": "Electronics",
      "alias": "electronics",
      "image": "/category_images/electronics.png",
      "enabled": true
    }
  ],
  "productDTO": {
    "id": 101,
    "name": "iPhone 15",
    "alias": "iphone-15",
    "categoryId": 5,
    "brandId": 2,
    "enabled": true,
    "inStock": true,
    "cost": 700.0,
    "price": 999.0,
    "discountPercent": 10.0,
    "shortDescription": "Short description",
    "fullDescription": "Full description",
    "mainImage": "/product_images/iphone-15.png",
    "productImages": [
      { "name": "/product_images/iphone-15-1.png" }
    ],
    "productDetails": [
      { "name": "Color", "value": "Black" }
    ],
    "length": 0.0,
    "width": 0.0,
    "height": 0.0,
    "weight": 0.0
  }
}
```

### GET `/products/search/{keyword}`

Full-text search by keyword.

**Query params**
- `page`
- `size`
- `sort`

**Response 200**
```json
{
  "content": [
    {
      "id": 101,
      "name": "iPhone 15",
      "alias": "iphone-15",
      "price": 999.0,
      "priceAfterDiscount": 899.0,
      "mainImage": "/product_images/iphone-15.png"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

## General settings

### GET `/general-sittings`

Returns general/currency settings for the app startup flow.

**Response 200**
```json
[
  {
    "key": "SITE_NAME",
    "value": "Hadaka Electro",
    "category": "GENERAL"
  }
]
```

## Frontend integration notes

- Use `withCredentials: true` for session-aware calls.
- Spring Data pagination is used on category and product list/search endpoints.
- Image fields are returned as relative paths such as `/product_images/...` and `/category_images/...`.
- The current security config disables CORS, so Angular should either run behind a same-origin proxy/reverse proxy or the backend CORS config must be aligned before cross-origin calls will work.
- Backend base path already includes `/ElectroCustomer`.
