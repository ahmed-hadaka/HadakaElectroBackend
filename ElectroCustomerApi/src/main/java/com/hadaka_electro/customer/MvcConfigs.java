package com.hadaka_electro.customer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class MvcConfigs implements WebMvcConfigurer {

    @Value("${app.category-images.path}")
    private String categoryImagesPath;

    @Value("${app.brand-logos.path}")
    private String brandLogosPath;

    @Value("${app.product-images.path}")
    private String productImagesPath;

    @Value("${app.default-images.path}")
    private String defaultImagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        mappingHelper(registry, "/default_images/**", defaultImagesPath);

        mappingHelper(registry, "/category_images/**", categoryImagesPath);

        mappingHelper(registry, "/brand_logos/**", brandLogosPath);

        mappingHelper(registry, "/product_images/**", productImagesPath);
    }

    private void mappingHelper(ResourceHandlerRegistry registry, String resourceHandler, String resourceLocation) {

        registry.addResourceHandler(resourceHandler).addResourceLocations(Paths.get(resourceLocation).toUri().toString());

    }
}