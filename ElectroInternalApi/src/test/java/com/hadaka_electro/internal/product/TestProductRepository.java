package com.hadaka_electro.internal.product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.hadaka_electro.internal.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.hadaka_electro.common.entities.Brand;
import com.hadaka_electro.common.entities.Category;
import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.entities.product.ProductImages;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestProductRepository {

	@Autowired
    ProductRepository productRepository;

	@Autowired
    TestEntityManager entityManager;

	private Brand Dell, Acer;
	private Category Laptop, Desktop;
	private Product DellLaptop, AcerDesktop, AcerLaptop;

	@BeforeEach
	public void testCreateProducts() {
		Dell = entityManager.find(Brand.class, 38);
		Acer = entityManager.find(Brand.class, 37);

		Laptop = entityManager.find(Category.class, 6);
		Desktop = entityManager.find(Category.class, 5);

		DellLaptop = new Product("dell laptop", "dell alias", "short description", "full description", "image",
				LocalDateTime.now(),"ahmed@gmail.com");
		DellLaptop.setBrand(Dell);
		DellLaptop.setCategory(Laptop);

		DellLaptop.setProductImages(Set.of(new ProductImages("dellExtraImage1.png", DellLaptop),
				new ProductImages("dellExtraImage2.png", DellLaptop)));

		AcerDesktop = new Product("acer desktop", "acer desktop alias", "short description", "full description", "image",
				LocalDateTime.now(), "mohammed@gmail.com");
		AcerDesktop.setBrand(Acer);
		AcerDesktop.setCategory(Desktop);

		AcerDesktop.setProductImages(Set.of(new ProductImages("acerExtraImage1", AcerDesktop),
				new ProductImages("acerExtraImage2", AcerDesktop)));

		AcerLaptop = new Product("acer laptop", "acer laptop alias", "short description", "full description", "image",
				LocalDateTime.now(), "email@sdf.com2");
		AcerLaptop.setBrand(Acer);
		AcerLaptop.setCategory(Laptop);

		Set<ProductImages> acerImages = new HashSet<>();
		acerImages.add(new ProductImages("acerExtraImage1", AcerLaptop));
		acerImages.add(new ProductImages("acerExtraImage2", AcerLaptop));
		AcerLaptop.setProductImages(acerImages);

		productRepository.saveAll(List.of(DellLaptop, AcerDesktop, AcerLaptop));
	}

	@Test
	public void testFindAll() {
		Iterable<Product> products = productRepository.findAll();

		assertEquals(productRepository.findAll().size(), 3);

		System.out.println("=========================");
		products.forEach(product -> System.out.println(product.toString()));
		System.out.println("=========================");

	}

	@Test
	public void testGetProduct() {

		Optional<Product> product = productRepository.findByName("acer desktop");

		assertEquals(product.isPresent(), true);
		assertEquals(product.get().getBrand(), Acer);
		assertEquals(product.get().getCategory(), Desktop);
		Set<ProductImages> productImages = product.get().getProductImages();
		assertEquals(productImages.size(), 2);
		productImages.forEach(image -> image.toString());
		productImages.forEach(image -> System.out.println(image.getProduct().getName()));
	}

	@Test
	public void testUpdateProduct() {
		Optional<Product> product = productRepository.findByName("acer laptop");

		assertEquals(product.isPresent(), true);

		// before updating
		assertEquals(product.get().getBrand(), Acer);
		assertEquals(product.get().getCategory(), Laptop);
		assertEquals(product.get().getProductImages().size(), 2);

		product.get().setName("acer laptop updated");
		product.get().setBrand(Dell);
		product.get().setCategory(null);
		product.get().getProductImages().removeIf(prodImg -> prodImg.getName().equals("acerExtraImage1"));

		productRepository.save(product.get());

		// after updating
		assertEquals(product.get().getBrand(), Dell);
		assertEquals(product.get().getCategory(), null);
		assertEquals(product.get().getProductImages().size(), 1);

	}

	@Test
	public void testDeleteProduct() {

		Optional<Product> productBfrDeletion = productRepository.findByName("acer laptop");

		assertEquals(productBfrDeletion.isPresent(), true);

		productRepository.delete(productBfrDeletion.get());

		Optional<Product> productAfrDeletion = productRepository.findByName("acer laptop");

		assertEquals(productAfrDeletion.isEmpty(), true);
	}
}
