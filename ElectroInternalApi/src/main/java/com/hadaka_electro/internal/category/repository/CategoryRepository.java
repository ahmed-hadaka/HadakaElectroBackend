package com.hadaka_electro.internal.category.repository;

import com.hadaka_electro.common.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE CONCAT(c.id,' ',c.name,' ',c.alias) LIKE %:keyword%")
    Page<Category> findAll(String keyword, Pageable pageable);

    Page<Category> findAll(Pageable pageable);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    boolean existsByAlias(String alias);

    @Query("UPDATE Category c SET c.enabled =?2 WHERE c.id = ?1")
    @Modifying
    void updateEnableStatus(int id, boolean status);

    Optional<Category> findByAlias(String alias);
}
