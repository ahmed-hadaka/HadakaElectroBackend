package com.hadaka_electro.internal.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.hadaka_electro.internal.aspect.CheckObjectUniqueness;
import com.hadaka_electro.internal.category.dto.CategoryListDTO;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hadaka_electro.internal.category.repository.CategoryRepository;
import com.hadaka_electro.common.entities.Category;

@ExtendWith(MockitoExtension.class)
public class TestCategoryService {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CheckObjectUniqueness checkObjectUniqueness;

	@Mock
	private JoinPoint joinPoint;


	@Test
	public void testIsDuplicatedCategory_InEditMode_ShouldBeDuplicatedByName(){
		// existing category
		Category category = new Category(1,"computer","computerAlias");

		// to be edited category
		CategoryListDTO categoryListDTO = new CategoryListDTO(2,"computer","whatever");

		when(categoryRepository.findByName(categoryListDTO.getName())).thenReturn(Optional.of(category));
		when(joinPoint.getArgs()).thenReturn(new Object[]{categoryListDTO});

		DuplicatedObjectException exception
		 = assertThrows(DuplicatedObjectException.class, () -> checkObjectUniqueness.checkCategoryUniqueness(joinPoint));

		assertEquals("The NAME is Duplicated!",exception.getMessage());
	}

	@Test
	public void testIsDuplicatedCategory_InEditMode_ShouldBeDuplicatedByAlias() {
		// existing category
		Category category = new Category(1,"computer","computerAlias");

		// to be edited category
		CategoryListDTO categoryListDTO = new CategoryListDTO(2,"others","computerAlias");

		when(categoryRepository.findByAlias(categoryListDTO.getAlias())).thenReturn(Optional.of(category));
		when(joinPoint.getArgs()).thenReturn(new Object[]{categoryListDTO});

		DuplicatedObjectException exception
				= assertThrows(DuplicatedObjectException.class, () -> checkObjectUniqueness.checkCategoryUniqueness(joinPoint));

		assertEquals("The ALIAS is Duplicated!",exception.getMessage());
	}

	@Test
	public void testIsDuplicatedCategory_InCreationMode_ShouldBeDuplicatedByName() {
		// existing category
		Category category = new Category(1,"computer","computerAlias");

		// to be edited category
		CategoryListDTO categoryListDTO = new CategoryListDTO(0,"computer","whatever");

		when(categoryRepository.findByName(categoryListDTO.getName())).thenReturn(Optional.of(category));
		when(joinPoint.getArgs()).thenReturn(new Object[]{categoryListDTO});

		DuplicatedObjectException exception
				= assertThrows(DuplicatedObjectException.class, () -> checkObjectUniqueness.checkCategoryUniqueness(joinPoint));

		assertEquals("The NAME is Duplicated!",exception.getMessage());
	}

	@Test
	public void testIsDuplicatedCategory_InCreationMode_ShouldBeDuplicatedByAlias() {

		// existing category
		Category category = new Category(1,"computer","computerAlias");

		// to be edited category
		CategoryListDTO categoryListDTO = new CategoryListDTO(0,"others","computerAlias");

		when(categoryRepository.findByAlias(categoryListDTO.getAlias())).thenReturn(Optional.of(category));
		when(joinPoint.getArgs()).thenReturn(new Object[]{categoryListDTO});

		DuplicatedObjectException exception
				= assertThrows(DuplicatedObjectException.class, () -> checkObjectUniqueness.checkCategoryUniqueness(joinPoint));

		assertEquals("The ALIAS is Duplicated!",exception.getMessage());
	}

	@Test
	public void testIsDuplicatedCategory_InCreationMode_ShouldBeUnique() {

		// to be edited category
		CategoryListDTO categoryListDTO = new CategoryListDTO(0,"others","whatever");

		when(categoryRepository.findByName(categoryListDTO.getName())).thenReturn(Optional.empty());
		when(categoryRepository.findByAlias(categoryListDTO.getAlias())).thenReturn(Optional.empty());
		when(joinPoint.getArgs()).thenReturn(new Object[]{categoryListDTO});

		assertDoesNotThrow(()->checkObjectUniqueness.checkCategoryUniqueness(joinPoint));
	}

}
