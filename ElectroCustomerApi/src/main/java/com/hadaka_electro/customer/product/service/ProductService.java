package com.hadaka_electro.customer.product.service;

import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.entities.product.dto.ProductDTO;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.category.CategoryListDTO;
import com.hadaka_electro.customer.category.service.CategoryService;
import com.hadaka_electro.customer.product.ProductMapper;
import com.hadaka_electro.customer.product.dto.ProductListDTO;
import com.hadaka_electro.customer.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> listAllProductsByCategory(int catId, Pageable pageable) {

        Page<Product> productPage = productRepository
                .getAllProductsByCategoryAndSubCategory(catId, "-" + catId + "-", catId + "-", pageable);

        Page<ProductListDTO> productDTOS = productPage
                .map(product -> productMapper.toProductListDTO(product));

        List<CategoryListDTO> parentCategories = categoryService.getAllParentCategories(catId);

        return Map.of("parentCategories", parentCategories, "products", productDTOS);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProductById(int prodId) throws ObjectNotFoundException {
        Product product = findProductById(prodId);
        ProductDTO productDTO = productMapper.toProductDTO(product);

        int productCategoryId = product.getCategory().getId();

        List<CategoryListDTO> parentCategoriesList = categoryService.getAllParentCategories(productCategoryId);

        return Map.of("parentCategories", parentCategoriesList, "productDTO", productDTO);
    }

    private Product findProductById(int id) throws ObjectNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty())
            throw new ObjectNotFoundException("No Products with this id: " + id);
        return product.get();
    }

    public Page<ProductListDTO> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.fullTextSearchByKeyword(keyword, pageable);
        if (products.isEmpty()) {
            return Page.empty();
        } else {
            return products.map(product -> productMapper.toProductListDTO(product));
        }
    }
}
