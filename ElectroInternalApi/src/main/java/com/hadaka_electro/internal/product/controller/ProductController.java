package com.hadaka_electro.internal.product.controller;

import com.hadaka_electro.common.entities.product.dto.ProductDTO;
import com.hadaka_electro.common.entities.product.dto.ProductListDTO;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.brand.dto.BrandListDTO;
import com.hadaka_electro.internal.brand.service.BrandService;
import com.hadaka_electro.internal.category.dto.CategorySelectDTO;
import com.hadaka_electro.internal.category.service.CategoryService;
import com.hadaka_electro.internal.product.service.ProductService;
import com.hadaka_electro.internal.security.ElectroUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService,
                             BrandService brandService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Map<String, Object>> listAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @PageableDefault(size = 10, sort = "id", page = 0, direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ProductListDTO> products = productService.listAllProducts(keyword, categoryId, pageable);
        List<CategorySelectDTO> categories = categoryService.listAllCategories();

        return ResponseEntity.ok(Map.of(
                "products", products,
                "categories", categories
        ));
    }


    @GetMapping("/new-product")
    public ResponseEntity<Map<String, Object>> createNewProduct() throws ObjectNotFoundException {
        List<BrandListDTO> brands = brandService.listAllBrandsDropdown();
        List<CategorySelectDTO> categories = categoryService.listAllCategories();

        Map<String, Object> response = Map.of(
                "brands", brands,
                "categories", categories
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/save-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveProduct(
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "main_image", required = false) MultipartFile mainImage,
            @RequestPart(value = "extra_images", required = false) MultipartFile[] extraImages,
            @RequestParam(value = "removed_images_names", required = false) List<String> removedExtraImagesNames,
            @AuthenticationPrincipal ElectroUserDetails electroUserDetails)
            throws IOException, DuplicatedObjectException, ObjectNotFoundException {

        int id;
        if (electroUserDetails.hasRole("Salesperson")) { // Salesperson can edit prices only
            id = productService.savePrice(productDTO);
        } else {
            id = productService.saveProduct(productDTO, mainImage, extraImages, removedExtraImagesNames);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Product with ID " + id + " saved successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> viewProductDetails(@PathVariable int id) throws ObjectNotFoundException {
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editProduct(@PathVariable int id) throws ObjectNotFoundException {
        ProductDTO product = productService.findById(id);
        List<BrandListDTO> brands = brandService.listAllBrandsDropdown();
        List<CategorySelectDTO> categories = categoryService.listAllCategories();

        Map<String, Object> response = Map.of(
                "product", product,
                "brands", brands,
                "categories", categories
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-enable-status/{id}")
    public ResponseEntity<String> updateEnableStatus(@PathVariable int id)
            throws ObjectNotFoundException {

        boolean status = productService.updateEnableStatus(id);
        if (status) {
            return ResponseEntity.status(HttpStatus.OK).body("Product with id " + id + " has been enabled successfully");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Product with id " + id + " has been disabled successfully");

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) throws Exception {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product with ID " + id + " has been deleted.");
    }
}