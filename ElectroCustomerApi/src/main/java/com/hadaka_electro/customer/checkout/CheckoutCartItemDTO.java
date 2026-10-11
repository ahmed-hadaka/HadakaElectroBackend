package com.hadaka_electro.customer.checkout;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record CheckoutCartItemDTO(
        Integer productId,
        String productName,
        double unitPrice,
        @JsonIgnore
        double unitCost,
        int quantity,
        double shippingCost
) {
}
