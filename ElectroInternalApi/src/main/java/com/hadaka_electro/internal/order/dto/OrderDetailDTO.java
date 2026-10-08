package com.hadaka_electro.internal.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDTO {
    private Integer productId;
    private String productName;
    private String mainImage;
    private int quantity;
    private double productCost;
    private double unitPrice;
    private double shippingCost;
    private double subtotal;
}
