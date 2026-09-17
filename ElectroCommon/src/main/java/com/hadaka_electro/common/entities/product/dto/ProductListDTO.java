package com.hadaka_electro.common.entities.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductListDTO {
    private Integer id;
    private String name;
    private String categoryName;
    private String mainImage;
    private String brandName;
    private boolean enabled;
}
