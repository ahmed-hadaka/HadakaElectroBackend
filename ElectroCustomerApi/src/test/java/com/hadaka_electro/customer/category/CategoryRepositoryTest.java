package com.hadaka_electro.customer.category;

import com.hadaka_electro.common.entities.Category;
import com.hadaka_electro.customer.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getAllEnabledCategories() {
        Page<Category> enabledCategories = categoryRepository.getAllEnabledLeaveCategories(Pageable.ofSize(10));

        for (Category category : enabledCategories) {
            assertTrue(category.isEnabled());
        }
    }
}