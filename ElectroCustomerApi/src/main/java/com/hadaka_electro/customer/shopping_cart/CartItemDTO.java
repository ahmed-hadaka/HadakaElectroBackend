package com.hadaka_electro.customer.shopping_cart;

public record CartItemDTO(
        Integer productId,
        String productName,
        String mainImage,
        double price,
        int quantity
) {
}