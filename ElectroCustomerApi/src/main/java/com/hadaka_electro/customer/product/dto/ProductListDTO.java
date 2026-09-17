package com.hadaka_electro.customer.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductListDTO {
    private Integer id;
    private String name;
    private String alias;
    private double price;
    private double priceAfterDiscount;
    private String mainImage;
}
