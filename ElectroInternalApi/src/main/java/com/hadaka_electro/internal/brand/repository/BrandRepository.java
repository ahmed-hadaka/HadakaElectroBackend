package com.hadaka_electro.internal.brand.repository;

import com.hadaka_electro.common.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @Query("SELECT b FROM Brand b WHERE CONCAT(b.id, ' ', b.name) LIKE %:keyword%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    // Deletes the join table records before deleting the category
    @Modifying
    @Query(value = "DELETE FROM brands_categories WHERE category_id = :categoryId", nativeQuery = true)
    void deleteCategoryAssociations(@Param("categoryId") Integer categoryId);

    Page<Brand> findAll(Pageable pageable);

    Optional<Brand> findByName(String name);

    long countById(int id);

    boolean existsByName(String name);
}
