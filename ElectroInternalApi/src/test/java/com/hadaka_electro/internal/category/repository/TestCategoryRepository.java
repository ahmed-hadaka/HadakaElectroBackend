package com.hadaka_electro.internal.category.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.hadaka_electro.common.entities.Category;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

//By default, @DataJpaTest wraps every test in a transaction and rolls it back when the test finishes.
@DataJpaTest
// do tests against real db (mysql)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestCategoryRepository {

	@Autowired
	CategoryRepository categoryRepository;

	private Category parentCategory;
	private Category subCategory1;
	private Category subCategory2;

	@BeforeEach
	public void init() {
		parentCategory = new Category("computers", "computerAlias", "computers.png");
		subCategory1 = new Category("laptop", "laptop alias", "laptop.png");
		subCategory2 = new Category("Desktop", "desktop alias", "desktop.png");

		parentCategory.addAllChildren(Set.of(subCategory1, subCategory2));

		categoryRepository.save(parentCategory);
	}

	@Test
	public void testFindAll() {
		assertEquals(categoryRepository.findAll().size(), 3);
	}

	@Test
	public void testExistsByName() {
        assertTrue(categoryRepository.existsByName("computers"));
		assertTrue(categoryRepository.existsByName("laptop"));
	}

	@Test
	public void testExistsByAlias() {
		assertEquals(categoryRepository.existsByAlias("computerAlias"), true);
		assertEquals(categoryRepository.existsByAlias("desktop alias"), true);
	}

	@Test
	public void testCreateSubCategory() {

		// assert that all the entities is created
		assertThat(parentCategory.getId()).isGreaterThan(0);
		assertThat(subCategory2.getId()).isGreaterThan(0);

		// assert the establishment of parent-child(OneToMany) relationship
		assertThat(parentCategory.getChildren().size()).isGreaterThan(0);

		// assert the establishment of child-parent(ManyToOne) relationship
		assertEquals(subCategory1.getParent().getName(), "computers");
		Set<Category> children = parentCategory.getChildren();
		assertEquals(children.size(), 2);
	}

	@Test
	public void testGettingCategoriesAndPrintThem() {

		Category subCategory11 = new Category("CD", "Cd alias", "cd.png");

		Category subCategory22 = new Category("mouse", "mouse alias", "mouse.png");

		subCategory1.addChild(subCategory11);
		subCategory2.addChild(subCategory22);

		categoryRepository.saveAll(List.of(subCategory1, subCategory2));

		assertEquals(subCategory1.getParent().getName(), "computers");
		assertEquals(subCategory2.getParent().getName(), "computers");
		assertEquals(subCategory11.getParent().getName(), "laptop");
		assertEquals(subCategory22.getParent().getName(), "Desktop");

		List<Category> children = categoryRepository.findAll();
		for (Category root : children) {
			if (root.getParent() == null) {
				System.out.println(root.getName() + "\n" + "|");
				printHelper(root.getChildren());
			}
		}
	}

	private void printHelper(Set<Category> children) {
		for (Category category : children) {
			System.out.println("-- " + category.getName() + "\n" + "|");
			if (!category.getChildren().isEmpty()) {
				System.out.print("--");
				printHelper(category.getChildren());
			}
		}
	}

	@Test
	public void testDeleteSubCategory() {

		parentCategory.getChildren().removeIf(c -> c.getName().equals("laptop"));
		categoryRepository.deleteById(subCategory1.getId());

		assertEquals(categoryRepository.findByName("computers").isEmpty(), false);
		assertEquals(categoryRepository.findByName("laptop").isEmpty(), true);
		assertEquals(categoryRepository.findByName("Desktop").isEmpty(), false);
		assertEquals(parentCategory.getChildren().contains(subCategory1), false);
		assertEquals(parentCategory.getChildren().contains(subCategory2), true);
	}

	@Test
	public void testDeleteParentCategoryWithItsChildren() {

		// deleting parent category will delete all it's children.(cascade.ALL)
		categoryRepository.deleteById(parentCategory.getId());

		assertEquals(categoryRepository.findByName("computers").isEmpty(), true);
		assertEquals(categoryRepository.findByName("laptop").isEmpty(), true);
		assertEquals(categoryRepository.findByName("Desktop").isEmpty(), true);
	}

	@Test
	public void testUpdateParentCategory() {
		parentCategory.setEnabled(true);
		categoryRepository.save(parentCategory);

		assertEquals(categoryRepository.findById(parentCategory.getId()).get().isEnabled(), true);
		assertEquals(categoryRepository.findById(subCategory1.getId()).get().getParent().isEnabled(), true);
		assertEquals(categoryRepository.findById(subCategory2.getId()).get().getParent().isEnabled(), true);
	}

	@Test
	public void testUpdateSubCategory() {
		subCategory1.setEnabled(true);
		categoryRepository.save(parentCategory);

		assertEquals(categoryRepository.findById(parentCategory.getId()).get().getChildren().stream()
				.anyMatch(c -> c.getName().equals("laptop") && c.isEnabled() == true), true);
		assertEquals(categoryRepository.findById(parentCategory.getId()).get().getChildren().stream()
				.anyMatch(c -> c.getName().equals("laptop") && c.isEnabled() == false), false);
		assertEquals(categoryRepository.findById(subCategory1.getId()).get().isEnabled(), true);
	}
}
