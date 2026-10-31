package com.hadaka_electro.customer.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderReturnReqDTO(
        @NotNull
        Integer orderId,

        @NotBlank
        String reason,

        String note
) {
}
