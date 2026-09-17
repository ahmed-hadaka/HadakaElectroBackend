package com.hadaka_electro.common.entities.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailsDTO {

    @NotNull
    String name;
    @NotNull
    String value;

    public ProductDetailsDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
