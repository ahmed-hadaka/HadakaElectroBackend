package com.hadaka_electro.internal.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryListDTO {

    @NotNull(message = "category Id can not be null")
    private int id;

    @Size(min = 2, message = "The minimum Name length is 2")
    @NotBlank(message = "The Name can not be blank")
    private String name;

    @Size(min = 2, message = "The minimum Alias length is 2")
    @NotBlank(message = "The Alias can not be blank")
    private String alias;

    @NotBlank
    private String image;


    private CategorySelectDTO parent;

    private boolean enabled;

    public CategoryListDTO(int id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }
}
