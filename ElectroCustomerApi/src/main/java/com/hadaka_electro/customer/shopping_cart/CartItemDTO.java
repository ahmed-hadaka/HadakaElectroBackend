package com.hadaka_electro.customer.shopping_cart;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record CartItemDTO(
        Integer productId,
        String productName,
        String mainImage,
        double unitPrice,
        @JsonIgnore
        double unitCost,
        int quantity
) {
}