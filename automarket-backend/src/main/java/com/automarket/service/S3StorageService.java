package com.automarket.service;

import com.automarket.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3 storage implementation for production.
 * Files are uploaded to S3 and served via CloudFront CDN.
 * Active when {@code automarket.storage.provider=s3}.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "automarket.storage.provider", havingValue = "s3")
public class S3StorageService implements StorageService {

    @Value("${automarket.storage.s3.bucket}")
    private String bucket;

    @Value("${automarket.storage.s3.cdn-url}")
    private String cdnUrl;

    private final S3Client s3Client;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public UploadResult store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Cannot store empty file");
        }

        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
        String storageKey = folder + "/" + filename;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(storageKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String url = cdnUrl + "/" + storageKey;
            log.info("Uploaded to S3: {} -> {}", storageKey, url);
            return new UploadResult(storageKey, url);

        } catch (IOException e) {
            throw new StorageException("Failed to upload file to S3: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(storageKey)
                    .build());
            log.info("Deleted from S3: {}", storageKey);
        } catch (Exception e) {
            log.warn("Failed to delete from S3 {}: {}", storageKey, e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf('.'));
    }
}
