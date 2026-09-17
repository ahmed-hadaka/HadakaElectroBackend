package com.hadaka_electro.internal.product.repository;

import com.hadaka_electro.common.entities.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p " +
            "WHERE CONCAT(p.id, ' ', p.name,' ', p.alias,' ', p.shortDescription,' ', p.fullDescription)" +
            " LIKE %:keyword%")
    Page<Product> findAllByKeyword(String keyword, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.category.id = :categoryId OR p.category.parentIds LIKE %:categoryId%")
    Page<Product> findAllByCategory(String categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE" +
            " CONCAT(p.id, ' ', p.name,' ', p.alias,' ', p.shortDescription,' ', p.fullDescription)" +
            " LIKE %:keyword% AND(p.category.id = :categoryId OR p.category.parentIds LIKE %:categoryId%)")
    Page<Product> findAllInCategory(String keyword, String categoryId, Pageable pageable);

    long countById(int id);

    @Modifying
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    void updateEnableStatus(int id, boolean isEnabled);

    @Modifying
    @Query(value = "update products set category_id = NULL where category_id =:categoryId", nativeQuery = true)
    void detachCategoryFromProduct(@Param("categoryId") Integer categoryId);


    Optional<Product> findByAlias(String alias);

    @Modifying
    @Query(value = "UPDATE products set brand_id = null where brand_id =:brandId", nativeQuery = true)
    void DeleteAssociatedBrands(@Param("brandId") int brandId);
}