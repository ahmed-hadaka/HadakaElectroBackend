package com.hadaka_electro.internal.brand.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.hadaka_electro.common.entities.Brand;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


//By default, @DataJpaTest wraps every test in a transaction and rolls it back when the test finishes.
@DataJpaTest
// do tests against real db (mysql)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestBrandRepository {

	@Autowired
	BrandRepository brandRepository;

	private Brand acer;
	private Brand apple;

	@BeforeEach
	public void init() {
		acer = new Brand("acer", "acer logo");
		apple = new Brand("apple", "apple logo");

		brandRepository.saveAll(List.of(acer, apple));
	}

	@Test
	public void testFindAll() {
		List<Brand> brands = brandRepository.findAll();

		brands.forEach(System.out::println);

		assertEquals(brands.size(), 2);
		assertEquals(brandRepository.findByName("apple").isPresent(), true);
		assertEquals(brandRepository.findByName("acer").isPresent(), true);
		assertEquals(brandRepository.findByName("orange").isPresent(), false);
	}

	@Test
	public void testUpdateBrand() {
		acer.setLogo("another acer logo");

		brandRepository.save(acer);
		assertEquals(brandRepository.findByName("acer").get().getLogo(), "another acer logo");
	}

	@Test
	public void testDeleteBrand() {
		brandRepository.delete(acer);
		assertEquals(brandRepository.findAll().size(), 1);
		brandRepository.delete(apple);
		assertEquals(brandRepository.findAll().size(), 0);
	}
}
