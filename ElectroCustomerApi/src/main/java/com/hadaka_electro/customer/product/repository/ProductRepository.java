package com.hadaka_electro.customer.product.repository;

import com.hadaka_electro.common.entities.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("select p from Product p where p.enabled=true and" +
            " (p.category.id = ?1 or (p.category.parentIds like %?2% " +
            "or p.category.parentIds like ?3%))")
//..-5-.. or 5-....
    Page<Product> getAllProductsByCategoryAndSubCategory(int catId, String midCatId, String startCatId, Pageable pageable);


    @Query(value = "select * from products " +
            "where match(name, full_description, short_description)" +
            " against(:keyword)"
            , nativeQuery = true
    )
    Page<Product> fullTextSearchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
