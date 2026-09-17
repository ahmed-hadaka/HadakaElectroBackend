package com.hadaka_electro.customer.category;


import com.hadaka_electro.common.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {


    public CategoryListDTO toCategoryListDTO(Category category) {
        CategoryListDTO dto = new CategoryListDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setAlias(category.getAlias());
        dto.setImage(category.getImagePath());
        dto.setEnabled(category.isEnabled());
        return dto;
    }}