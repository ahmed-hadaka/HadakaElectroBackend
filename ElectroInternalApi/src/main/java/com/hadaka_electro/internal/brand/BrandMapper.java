package com.hadaka_electro.internal.brand;

import com.hadaka_electro.internal.brand.dto.BrandDTO;
import com.hadaka_electro.internal.brand.dto.BrandListDTO;
import com.hadaka_electro.internal.category.dto.CategorySelectDTO;
import com.hadaka_electro.internal.category.repository.CategoryRepository;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.common.entities.Brand;
import com.hadaka_electro.common.entities.Category;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BrandMapper {

    CategoryRepository categoryRepository;

    public BrandMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public BrandListDTO toListDTO(Brand brand) throws ObjectNotFoundException {
        BrandListDTO brandListDTO;
        if(brand != null){
            brandListDTO = new BrandListDTO();
            brandListDTO.setId(brand.getId());
            brandListDTO.setName(brand.getName());
        }else{
            throw new ObjectNotFoundException("Can't map Brand to BrandListDTO, Brand is null");
        }
        return brandListDTO;
    }

    public BrandDTO toDTO(Brand brand) {
        if (brand == null) {
            return null;
        }

        BrandDTO dto = new BrandDTO();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setLogo(brand.getLogo());

        if (brand.getCategories() != null) {
            dto.setCategories(brand.getCategories().stream()
                    .map(c -> new CategorySelectDTO(c.getId(), c.getName()))
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public Brand toEntity(BrandDTO dto, Brand existingBrand) {
        Brand brand = (existingBrand != null) ? existingBrand : new Brand();


        brand.setName(dto.getName());
        brand.setCategories(
                dto.getCategories().stream().map(cat ->{
                    try {
                       Category category = categoryRepository.findById(cat.getId())
                                .orElseThrow( () -> new ObjectNotFoundException("No category with this id: "+cat.getId()));
                       return category;
                    } catch (ObjectNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
        ).collect(Collectors.toSet()));

        return brand;
    }
}