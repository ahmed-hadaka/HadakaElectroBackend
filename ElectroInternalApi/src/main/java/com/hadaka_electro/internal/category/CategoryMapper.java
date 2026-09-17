package com.hadaka_electro.internal.category;

import com.hadaka_electro.common.entities.Category;
import com.hadaka_electro.internal.category.dto.CategoryListDTO;
import com.hadaka_electro.internal.category.dto.CategorySelectDTO;
import com.hadaka_electro.internal.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryMapper {

    @Autowired
    CategoryRepository categoryRepository;

    public CategoryListDTO toCategoryListDTO(Category category) {
        CategoryListDTO dto = new CategoryListDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setAlias(category.getAlias());
        dto.setImage(category.getImage());
        if (category.getParent() != null) {
            CategorySelectDTO categorySelectDTO = new CategorySelectDTO(category.getParent().getId(),
                    category.getParent().getName());
            dto.setParent(categorySelectDTO);
        }
        dto.setEnabled(category.isEnabled());
        return dto;
    }

    public Category toEntity(CategoryListDTO categoryListDTO, Category existingCategory) {
        if (existingCategory != null) {
            return getCategory(categoryListDTO, existingCategory);
        } else {
            Category category = new Category();
            return getCategory(categoryListDTO, category);
        }
    }


    private Category getCategory(CategoryListDTO categoryListDTO, Category existingCategory) {
        existingCategory.setName(categoryListDTO.getName());
        existingCategory.setAlias(categoryListDTO.getAlias());
        if (categoryListDTO.getParent() != null) {
            Optional<Category> parentCategory = categoryRepository.findById(categoryListDTO.getParent().getId());
            existingCategory.setParent(parentCategory.get());
        }

        existingCategory.setEnabled(categoryListDTO.isEnabled());
        return existingCategory;
    }
}
