package com.hadaka_electro.internal.brand.service;

import com.hadaka_electro.common.entities.Brand;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.brand.BrandMapper;
import com.hadaka_electro.internal.brand.dto.BrandDTO;
import com.hadaka_electro.internal.brand.dto.BrandListDTO;
import com.hadaka_electro.internal.brand.repository.BrandRepository;
import com.hadaka_electro.internal.category.repository.CategoryRepository;
import com.hadaka_electro.internal.product.repository.ProductRepository;
import com.hadaka_electro.internal.utils.FileUtil;
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
public class BrandService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final BrandMapper brandMapper;
    private ProductRepository productRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository, ProductRepository productRepository, CategoryRepository categoryRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.brandMapper = brandMapper;
        this.productRepository = productRepository;
    }

    public List<BrandDTO> listAllBrands() {
        return brandRepository.findAll(Sort.by("name").ascending()).stream()
                .map(brandMapper::toDTO)
                .toList();
    }

    public List<BrandListDTO> listAllBrandsDropdown() throws ObjectNotFoundException {
        List<Brand> brands = brandRepository.findAll(Sort.by("name").ascending());
        List<BrandListDTO> brandListDTOS = new ArrayList<>();
        for (Brand brand : brands) {
            brandListDTOS.add(brandMapper.toListDTO(brand));
        }
        return brandListDTOS;
    }

    public Page<BrandDTO> listAllBrands(String keyword, Pageable pageable) {
        Page<Brand> brandPage;
        if (keyword != null && !keyword.isEmpty())
            brandPage = brandRepository.findAll(keyword, pageable);
        else
            brandPage = brandRepository.findAll(pageable);
        return brandPage.map(brandMapper::toDTO);
    }


    @Transactional
    public int saveBrand(BrandDTO brandDTO, MultipartFile multipartFile)
            throws IOException, DuplicatedObjectException, ObjectNotFoundException {

        Optional<Brand> existedBrand = brandRepository.findById(brandDTO.getId());
        Brand brand;
        if (existedBrand.isPresent()) {
            brand = brandMapper.toEntity(brandDTO, existedBrand.get());
        } else {
            brand = brandMapper.toEntity(brandDTO, null);
        }


        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand savedBrand = brandRepository.save(brand);
            String uploadDir = "brand_logos/" + savedBrand.getId();

            FileUtil.cleanDir(uploadDir);
            FileUtil.saveFile(uploadDir, fileName, multipartFile);
        } else if (existedBrand.isPresent()) {
            brand.setLogo(existedBrand.get().getLogo());
            brandRepository.save(brand);
        }
        return brand.getId();
    }


    public BrandDTO findById(int id) throws ObjectNotFoundException {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No brand found with ID: " + id));
        return brandMapper.toDTO(brand);
    }


    @Transactional
    public void deleteBrand(int id) throws Exception {
        if (brandRepository.countById(id) > 0) {
            productRepository.DeleteAssociatedBrands(id);
            brandRepository.deleteById(id);
            FileUtil.deletePhotosDir("brand_logos/", id);
        } else
            throw new ObjectNotFoundException("No brand found with ID: " + id);
    }

}