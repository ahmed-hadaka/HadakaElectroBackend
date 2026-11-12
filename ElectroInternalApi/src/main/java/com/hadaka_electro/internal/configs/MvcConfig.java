package com.hadaka_electro.internal.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class MvcConfig implements WebMvcConfigurer {

    @Value("${app.user-photos.path}")
    private String userPhotosPath;

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

        mappingHelper(registry, "/user_photos/**", userPhotosPath);
        mappingHelper(registry, "/default_images/**", defaultImagesPath);

        // category images will be stored at the project class path.
        mappingHelper(registry, "/category_images/**", categoryImagesPath);

        mappingHelper(registry, "/brand_logos/**", brandLogosPath);

        mappingHelper(registry, "/product_images/**", productImagesPath);
    }

    private void mappingHelper(ResourceHandlerRegistry registry, String resourceHandler, String resourceLocation) {
        String uri;
        if (resourceLocation.startsWith("http://") || resourceLocation.startsWith("https://")) {
            uri = resourceLocation;
        } else {
            uri = Paths.get(resourceLocation).toAbsolutePath().toUri().toString();
        }
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        registry.addResourceHandler(resourceHandler).addResourceLocations(uri);
    }

}
