package com.hadaka_electro.internal.order.dto;

import com.hadaka_electro.common.entities.order.OrderStatus;
import com.hadaka_electro.common.entities.order.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderDTO(
        @NotNull
        Integer id,

        @NotNull(message = "Order time is required")
        LocalDateTime orderTime,

        @NotNull
        Long customerId,
        @NotBlank
        String customerName,

        @Email(message = "Customer email must be a valid email format")
        String customerEmail,

        @Size(max = 15, message = "Customer phone number must not exceed 15 characters")
        String customerPhoneNumber,

        @NotBlank(message = "Recipient name is required")
        @Size(max = 45, message = "Recipient name must not exceed 45 characters")
        String recipientName,

        @NotBlank(message = "Recipient phone number is required")
        @Size(max = 15, message = "Recipient phone number must not exceed 15 characters")
        String recipientPhoneNumber,

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 64, message = "Address line 1 must not exceed 64 characters")
        String addressLine1,

        @Size(max = 64, message = "Address line 2 must not exceed 64 characters")
        String addressLine2,

        @NotBlank(message = "City is required")
        @Size(max = 45, message = "City must not exceed 45 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 45, message = "State must not exceed 45 characters")
        String state,

        @NotBlank(message = "Postal code is required")
        @Size(max = 10, message = "Postal code must not exceed 10 characters")
        String postalCode,

        @NotBlank(message = "Country is required")
        @Size(max = 45, message = "Country must not exceed 45 characters")
        String country,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Order status is required")
        OrderStatus status,

        @NotNull(message = "Deliver days is required")
        @Min(value = 0, message = "Deliver days cannot be negative")
        Integer deliverDays,

        LocalDate expectedDeliverDate,

        @PositiveOrZero(message = "Shipping cost cannot be negative")
        double shippingCost,

        @PositiveOrZero(message = "Product cost cannot be negative")
        double productCost,

        @PositiveOrZero(message = "Subtotal cannot be negative")
        double subtotal,

        @PositiveOrZero(message = "Total cannot be negative")
        double total,

        double tax,

        @Valid
        Set<OrderDetailDTO> orderDetails,

        @Valid
        Set<OrderTrackDTO> orderTracks
) {
}