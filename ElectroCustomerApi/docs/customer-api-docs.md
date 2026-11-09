# Customer API Docs

**Last Updated:** November 8, 2026

Base URL: `/ElectroCustomer`

These endpoints are the ones the Angular frontend should use. The API is session-based, so browser requests must include cookies (`withCredentials: true` in Angular) for login/logout flows.

## Authentication

### GET `/auth/`

Initializes the session and returns the CSRF token used by state-changing requests.

**Response 200**
```json
{
  "token": "csrf-token-value",
  "parameterName": "_csrf",
  "headerName": "X-CSRF-TOKEN"
}
```

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

### GET `/customers/{email}`

Returns the authenticated customer's profile. The email in the path must match the
logged-in customer's email (case-insensitive).

**Authentication:** Required

**Responses**
- `200 OK`: `CustomerDTO`
- `401 Unauthorized`: no customer session
- `403 Forbidden`: the path email belongs to another customer

### POST `/customers/update-customer`

Updates the authenticated customer's profile.

**Authentication:** Required

**Body:** `CustomerDTO`

**Response 200**
```json
{
  "message": "Updated successfully"
}
```

The email in the request body must match the logged-in customer's email.

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
```json
{
  "message": "Customer: John Doe Registered successfully\nPlease check your email to verify your account"
}
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

## Shopping cart

### GET `/cart`

Returns the current customer's cart items.

**Response 200**
```json
[
  {
    "productId": 101,
    "productName": "iPhone 15",
    "mainImage": "/product_images/iphone-15.png",
    "unitPrice": 999.0,
    "quantity": 2
  }
]
```

### POST `/cart/add/{productId}/{quantity}`

Adds a product to the cart.

### PUT `/cart/update/{productId}/{quantity}`

Updates the cart quantity for a product.

### DELETE `/cart/remove/{productId}`

Removes a product from the cart.

### GET `/cart/check-shipping-availability`

Checks whether shipping is available for the current customer's cart.

**Authentication:** Required

**Response 200**
```json
{
  "message": true
}
```

## Address book

### GET `/address-book`

Returns the current customer's saved addresses.

**Response 200**
```json
{
  "addresses": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "phoneNumber": "1234567890",
      "addressLine1": "Street 1",
      "addressLine2": "",
      "city": "Riyadh",
      "state": "Riyadh",
      "postalCode": "12345",
      "defaultForShipping": true,
      "countryId": 1,
      "countryName": "Saudi Arabia"
    }
  ]
}
```

### GET `/address-book/{id}`

Returns a single address record.

### POST `/address-book/save`

Creates or updates an address.

### PATCH `/address-book/set-default/{id}`

Marks an address as the default shipping address.

### DELETE `/address-book/delete/{id}`

Deletes an address.

## Checkout

### GET `/checkout`

Initializes checkout for the authenticated customer.

**Response 200**
```json
{
  "defaultShippingAddress": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "1234567890",
    "addressLine1": "Street 1",
    "addressLine2": "",
    "city": "Riyadh",
    "state": "Riyadh",
    "postalCode": "12345",
    "defaultForShipping": true,
    "countryId": 1,
    "countryName": "Saudi Arabia"
  },
  "orderSummaryInfo": {
    "checkoutCartItemDTOS": [
      {
        "productId": 101,
        "productName": "iPhone 15",
        "unitPrice": 999.0,
        "quantity": 2,
        "shippingCost": 20.0
      }
    ],
    "orderTotalPrice": 1998.0,
    "shippingTotalPrice": 20.0,
    "tax": 0.0,
    "paymentTotal": 2018.0,
    "CODSupported": true,
    "deliverDays": 3,
    "expectedDeliverDate": "2026-11-11"
  }
}
```

### POST `/checkout/place-order`

Places an order using cash on delivery and sends the order confirmation email.

**Authentication:** Required

**Request:** No body or query parameters.

**Response:** `201 Created` with an empty body.

## Orders

All order endpoints require an authenticated customer session.

### GET `/orders` or `/orders/`

Returns the authenticated customer's orders.

**Query params**
- `keyword` (optional)
- `page`, `size`, `sort`
- Default sort: `orderTime,DESC`

**Response 200:** `Page<OrderListDTO>`
```json
{
  "content": [
    {
      "id": 1,
      "orderTime": "2026-11-08T10:30:00",
      "productsNames": "iPhone 15, Case",
      "total": 2018.0,
      "status": "NEW"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

### GET `/orders/{id}`

Returns the order details for the authenticated customer. Customers can only
retrieve their own orders.

**Response 200:** `OrderDTO`

### POST `/orders/return`

Requests a return for one of the authenticated customer's orders.

**Body**
```json
{
  "orderId": 1,
  "reason": "Damaged item",
  "note": "Optional additional note"
}
```

**Response:** `200 OK` with an empty body.

## General settings

### GET `/general-settings`

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
- Cart, address-book, and checkout endpoints require an authenticated customer session.
- CORS allows `http://localhost:4200` with credentials. Angular requests must use `withCredentials: true`.
- CSRF uses the readable `XSRF-TOKEN` cookie; send its value in the `X-XSRF-TOKEN` header for state-changing requests.
- Public routes include authentication, category/product browsing, general settings, registration, verification, and password-reset endpoints. Cart, address-book, checkout, orders, and customer-profile routes require authentication.
- Backend base path already includes `/ElectroCustomer`.
