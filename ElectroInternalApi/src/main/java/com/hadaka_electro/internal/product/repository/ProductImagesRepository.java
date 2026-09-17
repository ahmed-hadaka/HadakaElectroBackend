package com.hadaka_electro.internal.product.repository;

import com.hadaka_electro.common.entities.product.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, Integer> {

}
