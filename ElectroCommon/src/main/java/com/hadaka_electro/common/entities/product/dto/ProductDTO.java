package com.hadaka_electro.common.entities.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ProductDTO {

    private Integer id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3, max = 256, message = "Product name must be between 2 and 256 characters")
    private String name;

    @NotBlank(message = "Product alias cannot be blank")
    @Size(min = 3, max = 256, message = "Product alias must be between 2 and 256 characters")
    private String alias;

    @NotNull(message = "Product must belong to a category")
    private Integer categoryId;

    @NotNull(message = "Product must belong to a brand")
    private Integer brandId;

    private boolean enabled;
    private boolean inStock;

    private double cost;
    private double price;
    private double discountPercent;

    @Size(min = 5, message = "The minimum sh-desc length is 5")
    @NotBlank(message = "The sh-desc can not be blank")
    private String shortDescription;

    @Size(min = 10, message = "The minimum full-desc length is 10")
    private String fullDescription;

    @NotBlank
    private String mainImage;

    private Set<ProductImagesDTO> productImages = new HashSet<>();

    private Set<ProductDetailsDTO> productDetails = new HashSet<>();

    private double length;

    private double width;

    private double height;

    private double weight;

    public ProductDTO() {
    }

}