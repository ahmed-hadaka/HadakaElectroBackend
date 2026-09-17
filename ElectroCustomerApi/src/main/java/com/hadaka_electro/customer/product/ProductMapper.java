package com.hadaka_electro.customer.product;

import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.entities.product.ProductDetails;
import com.hadaka_electro.common.entities.product.dto.ProductDetailsDTO;
import com.hadaka_electro.common.entities.product.ProductImages;
import com.hadaka_electro.common.entities.product.dto.ProductImagesDTO;
import com.hadaka_electro.customer.product.dto.ProductListDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import com.hadaka_electro.common.entities.product.dto.ProductDTO;
@Component
public class ProductMapper {

    public ProductListDTO toProductListDTO(Product product) {
        ProductListDTO productListDTO = new ProductListDTO();
        if(product != null){
            productListDTO.setId(product.getId());
            productListDTO.setName(product.getName());
            productListDTO.setAlias(product.getAlias());
            productListDTO.setPrice(product.getPrice());
            productListDTO.setPriceAfterDiscount(product.getPriceAfterDiscount());
            productListDTO.setMainImage(product.getMainImagePath());
        }
        return productListDTO;
    }

    public ProductDTO toProductDTO(Product product) {
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
        dto.setMainImage(product.getMainImagePath());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        if (product.getBrand() != null) {
            dto.setBrandId(product.getBrand().getId());
        }
        Set<ProductImages> productImages =product.getProductImages();
        if(productImages != null && !productImages.isEmpty()){
            dto.setProductImages(productImages.stream().map(pi ->
                            new ProductImagesDTO(pi.getImagePath())
                    ).collect(Collectors.toSet())
            );
        }

        Set<ProductDetails> productDetails = product.getProductDetails();
        if(productDetails != null && !productDetails.isEmpty()){
            dto.setProductDetails(productDetails.stream().map(pd ->
                            new ProductDetailsDTO( pd.getName(),pd.getValue())
                    ).collect(Collectors.toSet())
            );
        }

        dto.setLength(product.getLength());
        dto.setWidth(product.getWidth());
        dto.setHeight(product.getHeight());
        dto.setWeight(product.getWeight());

        return dto;
    }
}
