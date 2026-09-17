package com.hadaka_electro.customer.category.service;

import com.hadaka_electro.common.entities.Category;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.category.CategoryListDTO;
import com.hadaka_electro.customer.category.CategoryMapper;
import com.hadaka_electro.customer.category.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional(readOnly = true)
    public Page<CategoryListDTO> listAllLeaveCategories(Pageable pageable) {

        Page<Category> enabledLeaveCategories = categoryRepository.getAllEnabledLeaveCategories(pageable);

        return enabledLeaveCategories
                .map(enabledLeaveCategory -> categoryMapper.toCategoryListDTO(enabledLeaveCategory));
    }

    @Transactional(readOnly = true) // read only optimize hibernate performance by disabling dirty-checking.
    public List<CategoryListDTO> getAllParentCategories(int catId) throws ObjectNotFoundException {
        Category category = getCategoryById(catId);

        List<CategoryListDTO> categoryListDTOS = new ArrayList<>();
        if (category.getParentIds() != null) {
            String[] parentIds = category.getParentIds().split("-");// "4-1-" ==> ["4","1"]
            for (String parentId : parentIds) {
                CategoryListDTO categoryListDTO = categoryMapper.toCategoryListDTO(getCategoryById(Integer.valueOf(parentId)));
                categoryListDTOS.addFirst(categoryListDTO);
            }
        }
        categoryListDTOS.addLast(categoryMapper.toCategoryListDTO(category));

        return categoryListDTOS;
    }

    private Category getCategoryById(int catId) throws ObjectNotFoundException {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty())
            throw new ObjectNotFoundException("No Categories with this id: " + catId);
        return category.get();
    }
}
