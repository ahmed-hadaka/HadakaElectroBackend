package com.hadaka_electro.internal.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hadaka_electro.common.entities.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record OrderTrackDTO(
        @NotNull(message = "Order ID is required")
        Integer orderId,

        @NotNull(message = "Order status is required")
        OrderStatus orderStatus,

        String notes,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}