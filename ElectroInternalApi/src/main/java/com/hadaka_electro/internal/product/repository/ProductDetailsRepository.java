package com.hadaka_electro.internal.product.repository;

import com.hadaka_electro.common.entities.product.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Integer> {

}
