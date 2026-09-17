package com.hadaka_electro.customer.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryListDTO {

    private int id;

    private String name;

    private String alias;

    private String image;

    private boolean enabled;
}
