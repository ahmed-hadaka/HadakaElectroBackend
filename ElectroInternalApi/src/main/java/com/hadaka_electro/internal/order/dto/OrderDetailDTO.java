package com.hadaka_electro.internal.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderDetailDTO(
        @NotNull(message = "Product ID is required")
        Integer productId,

        String productName,
        String mainImage,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        @PositiveOrZero(message = "Product cost cannot be negative")
        double productCost,

        @PositiveOrZero(message = "Unit price cannot be negative")
        double unitPrice,

        @PositiveOrZero(message = "Shipping cost cannot be negative")
        double shippingCost,

        @PositiveOrZero(message = "Subtotal cannot be negative")
        double subtotal
) {
}