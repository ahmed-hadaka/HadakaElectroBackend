package com.hadaka_electro.common.entities.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImagesDTO {

    @NotNull
    private String name;

    public ProductImagesDTO(String name) {
        this.name = name;
    }
}
