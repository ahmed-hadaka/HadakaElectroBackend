package com.hadaka_electro.internal.product.service;

import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.entities.product.ProductImages;
import com.hadaka_electro.common.entities.product.dto.ProductDTO;
import com.hadaka_electro.common.entities.product.dto.ProductListDTO;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.product.ProductMapper;
import com.hadaka_electro.internal.product.repository.ProductImagesRepository;
import com.hadaka_electro.internal.product.repository.ProductRepository;
import com.hadaka_electro.internal.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private ProductImagesRepository productImagesRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductImagesRepository productImagesRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productImagesRepository = productImagesRepository;
    }

    public Page<ProductListDTO> listAllProducts(String keyword, Integer categoryId, Pageable pageable) {
        Page<Product> productPage;
        if (categoryId != null && categoryId > 0) {
            if (keyword != null && !keyword.isEmpty())
                productPage = productRepository.findAllInCategory(keyword, Integer.toString(categoryId), pageable);
            else
                productPage = productRepository.findAllByCategory(Integer.toString(categoryId), pageable);
        } else {
            if (keyword != null && !keyword.isEmpty())
                productPage = productRepository.findAllByKeyword(keyword, pageable);
            else
                productPage = productRepository.findAll(pageable);
        }
        return productPage.map(productMapper::toListDTO);
    }

    @Transactional
    public int saveProduct(ProductDTO productDTO, MultipartFile mainImage, MultipartFile[] extraImages, List<String> removedImagesNames)
            throws IOException, DuplicatedObjectException, ObjectNotFoundException {

        Optional<Product> existedProduct = productRepository.findById(productDTO.getId());
        Product product;

        product = productMapper.toEntity(productDTO, existedProduct);

        Product savedProduct = saveMainImage(mainImage, product, existedProduct);

        handleExtraImages(extraImages, savedProduct, removedImagesNames);

        return savedProduct.getId();
    }

    @Transactional
    public int savePrice(ProductDTO productDTO) throws ObjectNotFoundException {
        int id = productDTO.getId();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No Product with this id " + id));

        product.setCost(productDTO.getCost());
        product.setPrice(productDTO.getPrice());
        product.setDiscountPercent(productDTO.getDiscountPercent());

        // with @Transactional, productRepository.save(product) is optional
        // because Hibernate automatically flushes changes at transaction commit.
        productRepository.save(product);
        return id;
    }

    private Product saveMainImage(MultipartFile mainImage, Product product, Optional<Product> existedProduct) throws IOException, ObjectNotFoundException {
        Product savedProduct;
        if (mainImage != null && !mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            product.setMainImage(fileName);

            savedProduct = productRepository.save(product);
            String uploadDir = "product_images/" + savedProduct.getId();

            FileUtil.cleanDir(uploadDir);
            FileUtil.saveFile(uploadDir, fileName, mainImage);

        } else if (existedProduct.isPresent()) {
            product.setMainImage(existedProduct.get().getMainImage());
            savedProduct = productRepository.save(product);
        } else {
            throw new ObjectNotFoundException("Every product must have a main image!");
        }
        return savedProduct;
    }

    private void handleExtraImages(MultipartFile[] extraImages, Product savedProduct, List<String> removedImagesNames) throws IOException {
        int id = savedProduct.getId();
        if (removedImagesNames != null && !removedImagesNames.isEmpty()) {
            if (id > 0) {
                Set<ProductImages> curProductImages = savedProduct.getProductImages();
                if (curProductImages != null) {
                    curProductImages.removeIf(productImage -> removedImagesNames.contains(productImage.getName()));
                    String fileAbsolutePath = "E:/Intellij-workspace/HadakaElectroBackEnd/product_images/126/extras/";
                    removedImagesNames.forEach(imageName -> FileUtil.removeFileIfExists(fileAbsolutePath + imageName));
                }
            }
        }


        if (extraImages != null && extraImages.length > 0) {
            String uploadDir = "product_images/" + id + "/extras";
//            FileUtil.cleanDir(uploadDir);

            Set<ProductImages> newProductImages = new HashSet<>();
            for (MultipartFile extraImage : extraImages) {
                String fileName = StringUtils.cleanPath(extraImage.getOriginalFilename());
                ProductImages productImage = new ProductImages(fileName, savedProduct);
                newProductImages.add(productImage);
                FileUtil.saveFile(uploadDir, fileName, extraImage);
            }

            // supposing that dirt check will save productImages to product entity inside single transaction
            Set<ProductImages> curProductImages = savedProduct.getProductImages();
            if (curProductImages != null) {
                curProductImages.addAll(newProductImages);
            } else {
                savedProduct.setProductImages(newProductImages);
            }
            productImagesRepository.saveAll(newProductImages);
        }
    }

    public ProductDTO findById(int id) throws ObjectNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No product found with ID: " + id));
        return productMapper.toDTO(product);
    }


    @Transactional
    public void deleteProduct(int id) throws Exception {
        if (productRepository.countById(id) > 0) {
            productRepository.deleteById(id);
            FileUtil.deletePhotosDir("product_images/", id);
        } else {
            throw new ObjectNotFoundException("No product found with ID: " + id);
        }
    }

    @Transactional
    public boolean updateEnableStatus(int id) throws ObjectNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        boolean status = false;
        if (product.isPresent()) {
            status = product.get().getEnabled();
            productRepository.updateEnableStatus(id, !status);

        } else {
            throw new ObjectNotFoundException("Product not found with id: " + id);
        }
        return !status;
    }
}