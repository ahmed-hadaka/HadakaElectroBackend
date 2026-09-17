package com.hadaka_electro.internal.aspect;

import com.hadaka_electro.internal.brand.dto.BrandDTO;
import com.hadaka_electro.internal.brand.repository.BrandRepository;
import com.hadaka_electro.internal.category.dto.CategoryListDTO;
import com.hadaka_electro.internal.category.repository.CategoryRepository;
import com.hadaka_electro.internal.user.UserDTO;
import com.hadaka_electro.internal.user.repository.UserRepository;
import com.hadaka_electro.common.entities.User;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.entities.product.dto.ProductDTO;
import com.hadaka_electro.internal.product.repository.ProductRepository;
import com.hadaka_electro.common.entities.Brand;
import com.hadaka_electro.common.entities.Category;
import com.hadaka_electro.common.entities.product.Product;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Aspect
public class CheckObjectUniqueness {

    BrandRepository brandRepository;
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    public CheckObjectUniqueness(BrandRepository brandRepository,ProductRepository productRepository,UserRepository userRepository, CategoryRepository categoryRepository) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Before("execution(* com.hadaka_electro.internal.brand.service.BrandService.saveBrand(..))")
    public void checkBrandUniqueness(JoinPoint joinPoint) throws DuplicatedObjectException {
        Object[] args = joinPoint.getArgs();
        BrandDTO brandDTO = (BrandDTO) args[0];

        Optional<Brand> brand = brandRepository.findByName(brandDTO.getName());
        if (brand.isPresent()) {
            if(brand.get().getId() != brandDTO.getId()){
                throw new DuplicatedObjectException("The brand name " + brandDTO.getName() + " is already taken!");
            }
        }
    }


    @Before("execution(* com.hadaka_electro.internal.category.service.CategoryService.saveCategory(..))")
    public void checkCategoryUniqueness(JoinPoint joinPoint) throws DuplicatedObjectException {
        CategoryListDTO categoryDTO = (CategoryListDTO) joinPoint.getArgs()[0];

        Optional<Category> category1 = categoryRepository.findByName(categoryDTO.getName());

        if(category1.isPresent()){
            if(category1.get().getId() != categoryDTO.getId()){ // edit mode
                throw new DuplicatedObjectException("The NAME is Duplicated!");
            }
        }
        Optional<Category> category2 = categoryRepository.findByAlias(categoryDTO.getAlias());
        if(category2.isPresent()){
            if(category2.get().getId() != categoryDTO.getId()){ // edit mode
                throw new DuplicatedObjectException("The ALIAS is Duplicated!");
            }
        }
    }

    @Before("execution(* com.hadaka_electro.internal.product.service.ProductService.saveProduct(..))")
    public void checkProductUniqueness(JoinPoint joinPoint) throws DuplicatedObjectException {

        ProductDTO productDTO = (ProductDTO) joinPoint.getArgs()[0];
        int id = productDTO.getId();
        String name = productDTO.getName();
        String alias = productDTO.getAlias();

        Optional<Product> productByName = productRepository.findByName(name);
        if(productByName.isPresent()){
            if(productByName.get().getId() != id) {
                throw new DuplicatedObjectException("Product name " + name + "' is already taken!");
            }
        }
        Optional<Product> productByAlias = productRepository.findByAlias(alias);
        if(productByAlias.isPresent()){
            if(productByAlias.get().getId() != id)
                throw new DuplicatedObjectException("Product alias '" + alias + "' is already taken!");
        }
    }

    @Before("execution(* com.hadaka_electro.internal.user.service.UserService.saveUser(..))")
    public void checkEmailUniqueness(JoinPoint joinPoint) throws DuplicatedObjectException {
        Object[] args = joinPoint.getArgs();
        UserDTO userDTO = (UserDTO) args[0];

        int id = userDTO.getId();
        String email = userDTO.getEmail();

       Optional<User> user = userRepository.findByEmail(email);
       if(user.isPresent()){
           if(user.get().getId() != id)
               throw new DuplicatedObjectException("The email "+email+" is already taken!");
       }
    }
}

