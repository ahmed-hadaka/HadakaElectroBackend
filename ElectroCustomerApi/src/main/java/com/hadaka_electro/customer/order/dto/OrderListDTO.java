package com.hadaka_electro.customer.order.dto;

import com.hadaka_electro.common.entities.order.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.Set;

public record OrderListDTO(
        Integer id,

        @NotNull(message = "Order time is required")
        LocalDateTime orderTime,

        Set<String> productsNames,

        @PositiveOrZero(message = "Total cannot be negative")
        double total,

        @NotNull(message = "Order status is required")
        OrderStatus status
) {
}