package com.hadaka_electro.internal.brand.dto;

import com.hadaka_electro.internal.category.dto.CategorySelectDTO;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BrandDTO {
    @NotNull
    private Integer id;

    @Column(nullable = false, unique = true)
    @Size(min = 4, message = "The minimum Name length is 4")
    @NotBlank(message = "The Name can not be blank")
    private String name;

    @NotNull
    private String logo;

    private Set<CategorySelectDTO> categories = new HashSet<>();

    public BrandDTO() {
    }

    public BrandDTO(Integer id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

}