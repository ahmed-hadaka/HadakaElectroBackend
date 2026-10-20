package com.hadaka_electro.internal.order.dto;

import com.hadaka_electro.common.entities.order.OrderStatus;
import com.hadaka_electro.common.entities.order.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record OrderListDTO(
        Integer id,
        String customerName,

        @PositiveOrZero(message = "Total cannot be negative")
        double total,

        @NotNull(message = "Order time is required")
        LocalDateTime orderTime,

        String destination,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Order status is required")
        OrderStatus status
) {
}