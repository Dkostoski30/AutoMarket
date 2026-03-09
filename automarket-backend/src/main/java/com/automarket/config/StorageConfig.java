package com.automarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Serves locally stored files at /uploads/** when using the local storage provider.
 * In production (S3), this config is inactive and files are served via CloudFront.
 */
@Configuration
@ConditionalOnProperty(name = "automarket.storage.provider", havingValue = "local", matchIfMissing = true)
public class StorageConfig implements WebMvcConfigurer {

    @Value("${automarket.storage.local.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
