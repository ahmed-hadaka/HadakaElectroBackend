package com.hadaka_electro.customer.category.repository;

import com.hadaka_electro.common.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.enabled=true" +
            " AND c.children IS EMPTY ORDER BY c.name ASC")
    Page<Category> getAllEnabledLeaveCategories(Pageable pageable);


}
