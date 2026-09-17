package com.hadaka_electro.internal.category.dto;

import com.hadaka_electro.common.entities.Category;

import java.util.HashSet;
import java.util.Set;

public class CategorySelectDTO {


    private int id;
    private String name;

    public CategorySelectDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategorySelectDTO() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Set<CategorySelectDTO> mapping(Set<Category> categories) {
        Set<CategorySelectDTO> categorySelectDTOS = new HashSet<>();

        categories.forEach(cat -> categorySelectDTOS.add(new CategorySelectDTO(cat.getId(), cat.getName())));

        return categorySelectDTOS;
    }

}
