package com.hadaka_electro.internal.category.service;

import com.hadaka_electro.common.entities.Category;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.brand.repository.BrandRepository;
import com.hadaka_electro.internal.category.CategoryMapper;
import com.hadaka_electro.internal.category.dto.CategoryListDTO;
import com.hadaka_electro.internal.category.dto.CategorySelectDTO;
import com.hadaka_electro.internal.category.repository.CategoryRepository;
import com.hadaka_electro.internal.product.repository.ProductRepository;
import com.hadaka_electro.internal.utils.AmazonS3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final List<CategorySelectDTO> HierarchicalCategoriesList = new ArrayList<>();
    private final CategoryMapper categoryMapper;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository, BrandRepository brandRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    public List<CategorySelectDTO> listAllCategories() {
        return listCategoriesInHierarchicalForm(categoryRepository.findAll(), 0);
    }

    public List<CategorySelectDTO> listAllCategoriesExcept(int id) {
        return listCategoriesInHierarchicalForm(categoryRepository.findAll(), id);
    }

    public Page<CategoryListDTO> listAllCategories(String keyword, Pageable pageable) {
        Page<Category> categoryPage;
        if (keyword != null && !keyword.isEmpty())
            categoryPage = categoryRepository.findAll(keyword, pageable);
        else
            categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.map(cat -> categoryMapper.toCategoryListDTO(cat));
    }

    @Transactional
    public void saveCategory(CategoryListDTO categoryListDTO, MultipartFile multipartFile) throws IOException, DuplicatedObjectException {


        Optional<Category> existedCategory = categoryRepository.findById(categoryListDTO.getId());
        Category category;
        if (existedCategory.isPresent())
            category = categoryMapper.toEntity(categoryListDTO, existedCategory.get());
        else
            category = categoryMapper.toEntity(categoryListDTO, null);

        updateParentIds(category);

        if (multipartFile != null && !multipartFile.isEmpty()) {

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            category.setImage(fileName);


            categoryRepository.save(category);
            String uploadDir = "category_images/" + category.getId();

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

        } else if (existedCategory.isPresent()) {
            category.setImage(existedCategory.get().getImage());
            categoryRepository.save(category);
        }
    }

    private void updateParentIds(Category category) {
        StringBuilder parentIds = new StringBuilder();
        Category parent = category.getParent();
        if (parent != null) {
            parentIds.append(parent.getId()).append("-");
            if (parent.getParentIds() != null) {
                parentIds.append(parent.getParentIds());
            }
            category.setParentIds(parentIds.toString());
        }
    }

    public List<CategoryListDTO> findAllSorted() {
        return categoryRepository.findAll(Sort.by("name").ascending()).stream().map(
                categoryMapper::toCategoryListDTO
        ).toList();
    }

    public CategoryListDTO findById(int id) throws ObjectNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return category.map(categoryMapper::toCategoryListDTO).get();
        }
        throw new ObjectNotFoundException("No categories with this id: " + id);
    }


    @Transactional
    public void deleteCategory(int categoryId) throws Exception {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty())
            throw new ObjectNotFoundException("No categories with this id: " + categoryId);

        Category parent = category.get().getParent();

        if (parent != null) {
            parent.getChildren().removeIf(c -> c.getId() == categoryId);
        }

        productRepository.detachCategoryFromProduct(categoryId);
        brandRepository.deleteCategoryAssociations(categoryId);
        categoryRepository.deleteById(categoryId);
        AmazonS3Util.removeFolder("category_images/" + categoryId);
    }

    @Transactional
    public String updateEnableStatus(int id) throws ObjectNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty())
            throw new ObjectNotFoundException("No categories with this id: " + id);
        boolean status = category.get().isEnabled();
        categoryRepository.updateEnableStatus(id, !status);
        status = !status;
        if (status) {
            return "Enabled";
        }
        return "Disabled";
    }

    private List<CategorySelectDTO> listCategoriesInHierarchicalForm(List<Category> categories, int exceptCategoryId) {
        HierarchicalCategoriesList.clear();

        for (Category c : categories) {
            if (c.getParent() == null) {
                listHelper(c, exceptCategoryId, "", true);
            }
        }

        return HierarchicalCategoriesList;
    }

    private void listHelper(Category root, int exceptCategoryId, String indentation, boolean isRootCategory) {
        if (root.getId() == exceptCategoryId)
            return;
        if (isRootCategory)
            root.setName(indentation + root.getName().toUpperCase());
        else
            root.setName(indentation + root.getName());

        HierarchicalCategoriesList.add(new CategorySelectDTO(root.getId(), root.getName()));

        for (Category childCategory : root.getChildren()) {
            if (childCategory.getId() == exceptCategoryId)
                continue;
            if (!childCategory.getChildren().isEmpty()) {
                listHelper(childCategory, exceptCategoryId, indentation + "-", false);
            } else {
                childCategory.setName("-" + indentation + childCategory.getName());
                HierarchicalCategoriesList.add(new CategorySelectDTO(childCategory.getId(), childCategory.getName()));
            }
        }
    }

}
