package com.hadaka_electro.internal.brand.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.hadaka_electro.internal.aspect.CheckObjectUniqueness;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hadaka_electro.internal.brand.dto.BrandDTO;
import com.hadaka_electro.internal.brand.repository.BrandRepository;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.entities.Brand;

@ExtendWith(MockitoExtension.class)
public class CheckBrandUniquenessTest {

	@Mock
	private BrandRepository brandRepository;

	@Mock
	private JoinPoint joinPoint;

	@InjectMocks
	private CheckObjectUniqueness checkObjectUniqueness;

	@Test
	public void testCheckBrandUniqueness_inEditMode_shouldThrowExceptionWhenDuplicated() {
		BrandDTO brandDTO = new BrandDTO(2, "new name", "logo.png");
		Brand existingBrand = new Brand(1, "new name", "logo.png");

		when(joinPoint.getArgs()).thenReturn(new Object[]{ brandDTO });
		when(brandRepository.findByName(brandDTO.getName())).thenReturn(Optional.of(existingBrand));

		DuplicatedObjectException exception = assertThrows(
				DuplicatedObjectException.class,
				() -> checkObjectUniqueness.checkBrandUniqueness(joinPoint)
		);

		assertEquals("The brand name new name is already taken!", exception.getMessage());
	}

	@Test
	public void testCheckBrandUniqueness_inEditMode_shouldNotThrowExceptionWhenSameEntity() {
		BrandDTO brandDTO = new BrandDTO(2, "new name", "logo.png");
		Brand existingBrand = new Brand(2, "new name", "logo.png");

		when(joinPoint.getArgs()).thenReturn(new Object[]{ brandDTO });
		when(brandRepository.findByName(brandDTO.getName())).thenReturn(Optional.of(existingBrand));

		assertDoesNotThrow(() -> checkObjectUniqueness.checkBrandUniqueness(joinPoint));
	}

	@Test
	public void testCheckBrandUniqueness_inCreationMode_shouldThrowExceptionWhenDuplicated() {
		BrandDTO brandDTO = new BrandDTO(0, "apple", "logo.png");
		Brand existingBrand = new Brand(1, "apple", "logo.png");

		when(joinPoint.getArgs()).thenReturn(new Object[]{ brandDTO });
		when(brandRepository.findByName(brandDTO.getName())).thenReturn(Optional.of(existingBrand));

		DuplicatedObjectException exception = assertThrows(
				DuplicatedObjectException.class,
				() -> checkObjectUniqueness.checkBrandUniqueness(joinPoint)
		);

		assertEquals("The brand name apple is already taken!", exception.getMessage());
	}

	@Test
	public void testCheckBrandUniqueness_inCreationMode_shouldNotThrowExceptionWhenUnique() {
		BrandDTO brandDTO = new BrandDTO(0, "apple", "logo.png");

		when(joinPoint.getArgs()).thenReturn(new Object[]{ brandDTO });
		when(brandRepository.findByName(brandDTO.getName())).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> checkObjectUniqueness.checkBrandUniqueness(joinPoint));
	}
}