package com.hadaka_electro.internal.category.controller;

import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.category.CategoryCsvExporter;
import com.hadaka_electro.internal.category.dto.CategoryListDTO;
import com.hadaka_electro.internal.category.dto.CategorySelectDTO;
import com.hadaka_electro.internal.category.service.CategoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Page<CategoryListDTO>> listAllCategories(@RequestParam(required = false) String keyword,
                                                                   @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CategoryListDTO> categories = categoryService.listAllCategories(keyword, pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/new-category")
    public ResponseEntity<List<CategorySelectDTO>> createNewCategory() {
        List<CategorySelectDTO> categoriesSelectList = categoryService.listAllCategories();
        return ResponseEntity.ok(categoriesSelectList);
    }

    @PostMapping(value = "/save-category", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveCategory(@RequestPart("category") @Valid CategoryListDTO category,
                                               @RequestPart(value = "imageFile", required = false) MultipartFile multipartFile) throws IOException, DuplicatedObjectException {

        categoryService.saveCategory(category, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category Saved Successfully.");
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editCategory(@PathVariable int id) throws ObjectNotFoundException {
        CategoryListDTO category = categoryService.findById(id);
        // to select the parent category
        List<CategorySelectDTO> categorySelectDTOS = categoryService.listAllCategoriesExcept(id);

        Map<String, Object> response = Map.of(
                "category", category,
                "categories", categorySelectDTOS
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id) throws Exception {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category with id: " + id + " has been deleted.");
    }

    @PatchMapping("/toggle-enable-status/{id}")
    public ResponseEntity<String> updateEnableStatus(@PathVariable int id) throws ObjectNotFoundException {
        String status = categoryService.updateEnableStatus(id);
        return ResponseEntity.ok("Category id: " + id + " has been " + status + " successfully.");
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<CategoryListDTO> categories = categoryService.findAllSorted();
        CategoryCsvExporter csvExporter = new CategoryCsvExporter();
        csvExporter.export(categories, response);
    }

}
