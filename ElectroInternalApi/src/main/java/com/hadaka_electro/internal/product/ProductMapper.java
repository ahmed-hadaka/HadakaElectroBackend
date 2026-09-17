package com.hadaka_electro.internal.product;

import com.hadaka_electro.common.entities.Brand;
import com.hadaka_electro.common.entities.Category;
import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.entities.product.ProductDetails;
import com.hadaka_electro.common.entities.product.ProductImages;
import com.hadaka_electro.common.entities.product.dto.ProductDTO;
import com.hadaka_electro.common.entities.product.dto.ProductDetailsDTO;
import com.hadaka_electro.common.entities.product.dto.ProductImagesDTO;
import com.hadaka_electro.common.entities.product.dto.ProductListDTO;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.brand.repository.BrandRepository;
import com.hadaka_electro.internal.category.repository.CategoryRepository;
import com.hadaka_electro.internal.product.repository.ProductDetailsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    private final ProductDetailsRepository productDetailsRepository;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;

    public ProductMapper(CategoryRepository categoryRepository, BrandRepository brandRepository, ProductDetailsRepository productDetailsRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productDetailsRepository = productDetailsRepository;
    }

    public ProductListDTO toListDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductListDTO listDTO = new ProductListDTO();

        listDTO.setId(product.getId());
        listDTO.setName(product.getName());

        listDTO.setEnabled(product.getEnabled());

        listDTO.setMainImage(product.getMainImage());

        if (product.getCategory() != null) {
            listDTO.setCategoryName(product.getCategory().getName());
        } else {
            listDTO.setCategoryName("No categories chose");
        }

        if (product.getBrand() != null) {
            listDTO.setBrandName(product.getBrand().getName());
        } else {
            listDTO.setBrandName("No Brands chose");

        }

        return listDTO;
    }

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setAlias(product.getAlias());
        dto.setShortDescription(product.getShortDescription());
        dto.setFullDescription(product.getFullDescription());
        dto.setEnabled(product.getEnabled());
        dto.setInStock(product.getInStock());
        dto.setCost(product.getCost());
        dto.setPrice(product.getPrice());
        dto.setDiscountPercent(product.getDiscountPercent());
        dto.setMainImage(product.getMainImage());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        } else {
            dto.setCategoryId(0);
        }

        if (product.getBrand() != null) {
            dto.setBrandId(product.getBrand().getId());
        } else {
            dto.setBrandId(0);
        }
        Set<ProductImages> productImages = product.getProductImages();
        if (productImages != null && !productImages.isEmpty()) {
            dto.setProductImages(productImages.stream().map(pi ->
                            new ProductImagesDTO(pi.getName())
                    ).collect(Collectors.toSet())
            );
        }

        Set<ProductDetails> productDetails = product.getProductDetails();
        if (productDetails != null && !productDetails.isEmpty()) {
            dto.setProductDetails(productDetails.stream().map(pd ->
                            new ProductDetailsDTO(pd.getName(), pd.getValue())
                    ).collect(Collectors.toSet())
            );
        }

        dto.setLength(product.getLength());
        dto.setWidth(product.getWidth());
        dto.setHeight(product.getHeight());
        dto.setWeight(product.getWeight());

        return dto;
    }

    public Product toEntity(ProductDTO dto, Optional<Product> existingProduct) throws ObjectNotFoundException {
        Product product = existingProduct.orElseGet(() -> new Product());


        product.setName(dto.getName());
        product.setAlias(dto.getAlias());
        product.setShortDescription(dto.getShortDescription());
        product.setFullDescription(dto.getFullDescription());
        product.setEnabled(dto.isEnabled());
        product.setInStock(dto.isInStock());
        product.setCost(dto.getCost());
        product.setPrice(dto.getPrice());
        product.setDiscountPercent(dto.getDiscountPercent());

        Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        Brand brand = brandRepository.findById(dto.getBrandId()).orElse(null);

        product.setCategory(category);
        product.setBrand(brand);


        Set<ProductDetailsDTO> productDetailsDTO = dto.getProductDetails();

        if (productDetailsDTO != null && !productDetailsDTO.isEmpty()) {

            Set<ProductDetails> newProductDetails = productDetailsDTO.stream().map(pdet ->
                    new ProductDetails(pdet.getName(), pdet.getValue(), product)
            ).collect(Collectors.toSet());


            Set<ProductDetails> curProductDetails = product.getProductDetails();
            // existing one, to trigger orphan removal(deleting child entities upon removal from the parent list)
            if (curProductDetails != null) {
                curProductDetails.clear();
                curProductDetails.addAll(newProductDetails);
            } else {
                product.setProductDetails(newProductDetails);
            }
            productDetailsRepository.saveAll(newProductDetails);
        }

        product.setLength(dto.getLength());
        product.setWidth(dto.getWidth());
        product.setHeight(dto.getHeight());
        product.setWeight(dto.getWeight());

        return product;
    }
}