package com.hadaka_electro.customer;

import com.hadaka_electro.customer.category.CategoryListDTO;
import com.hadaka_electro.customer.category.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    CategoryService categoryService;

    public MainController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping({"/", "/list-categories"})
    public ResponseEntity<Page<CategoryListDTO>> homePage(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(categoryService.listAllLeaveCategories(pageable));
    }


}
