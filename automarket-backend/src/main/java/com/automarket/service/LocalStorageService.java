package com.automarket.service;

import com.automarket.exception.StorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Local filesystem storage for development and testing.
 * Files are served via Spring's static resource handler at /uploads/**.
 * Active when {@code automarket.storage.provider=local}.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "automarket.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${automarket.storage.local.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${automarket.storage.local.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    private Path rootPath;

    @PostConstruct
    public void init() {
        rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootPath);
            log.info("Local storage initialized at: {}", rootPath);
        } catch (IOException e) {
            throw new StorageException("Could not create upload directory: " + rootPath, e);
        }
    }

    @Override
    public UploadResult store(MultipartFile file, String folder) {
        validateFile(file);

        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
        String storageKey = folder + "/" + filename;
        Path targetPath = rootPath.resolve(storageKey).normalize();

        // Prevent path traversal
        if (!targetPath.startsWith(rootPath)) {
            throw new StorageException("Cannot store file outside upload directory");
        }

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath);
            String url = baseUrl + "/" + storageKey;
            log.debug("Stored file: {} -> {}", storageKey, url);
            return new UploadResult(storageKey, url);
        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Path file = rootPath.resolve(storageKey).normalize();
            if (!file.startsWith(rootPath)) {
                throw new StorageException("Invalid storage key: " + storageKey);
            }
            Files.deleteIfExists(file);
            log.debug("Deleted file: {}", storageKey);
        } catch (IOException e) {
            log.warn("Failed to delete file {}: {}", storageKey, e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessValidationException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessValidationException("Only image files are allowed");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessValidationException("File size exceeds 10MB limit");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf('.'));
    }

    // Inner exception for validation — will be caught by GlobalExceptionHandler as BusinessRuleException
    private static class BusinessValidationException extends com.automarket.exception.BusinessRuleException {
        BusinessValidationException(String msg) { super(msg); }
    }
}
