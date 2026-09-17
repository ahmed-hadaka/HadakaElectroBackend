package com.hadaka_electro.customer.product.controller;

import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.category.service.CategoryService;
import com.hadaka_electro.customer.product.dto.ProductListDTO;
import com.hadaka_electro.customer.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    ProductService productService;
    CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/c/{category-id}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(@PathVariable("category-id") int catId,
                                                                     @PageableDefault(sort = "name") Pageable pageable) throws ObjectNotFoundException {

        Map<String, Object> res = productService.listAllProductsByCategory(catId, pageable);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/p/{product-id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable("product-id") int prodId) throws ObjectNotFoundException {
        Map<String, Object> res = productService.getProductById(prodId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<Page<ProductListDTO>> searchProduct(@PathVariable String keyword,
                                                              @PageableDefault Pageable pageable) {
        Page<ProductListDTO> searchResult = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(searchResult);
    }


}
