package com.hadaka_electro.internal.brand.controller;

import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.brand.dto.BrandDTO;
import com.hadaka_electro.internal.brand.service.BrandService;
import com.hadaka_electro.internal.category.dto.CategorySelectDTO;
import com.hadaka_electro.internal.category.service.CategoryService;
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
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;
    private final CategoryService categoryService;

    @Autowired
    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Page<BrandDTO>> listAllBrands(@RequestParam(required = false) String keyword,
                                                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<BrandDTO> brands = brandService.listAllBrands(keyword, pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/new-brand")
    public ResponseEntity<List<CategorySelectDTO>> createNewBrand() {
        List<CategorySelectDTO> categoriesSelectList = categoryService.listAllCategories();
        return ResponseEntity.ok(categoriesSelectList);
    }

    @PostMapping(value = "/save-brand", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveBrand(@RequestPart("brand") @Valid BrandDTO brand,
                                            @RequestPart(value = "imageFile", required = false) MultipartFile multipartFile)
            throws IOException, DuplicatedObjectException, ObjectNotFoundException {

        int id = brandService.saveBrand(brand, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).body("Brand with id " + id + " Saved Successfully.");
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editBrand(@PathVariable int id) throws ObjectNotFoundException {
        BrandDTO brand = brandService.findById(id);
        List<CategorySelectDTO> categoriesSelectDTOS = categoryService.listAllCategories();

        Map<String, Object> response = Map.of(
                "brand", brand,
                "categories", categoriesSelectDTOS
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable int id) throws Exception {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Brand with id: " + id + " has been deleted.");
    }


}