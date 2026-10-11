package com.hadaka_electro.customer.checkout;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.List;

public record OrderSummaryInfoDTO(
        List<CheckoutCartItemDTO> checkoutCartItemDTOS,
        @JsonIgnore
        double orderTotalCost, // not shown to customer(for revenue)
        double orderTotalPrice,
        double shippingTotalPrice,
        double tax,
        double paymentTotal,
        boolean CODSupported,
        int deliverDays,
        LocalDate expectedDeliverDate
) {
}
