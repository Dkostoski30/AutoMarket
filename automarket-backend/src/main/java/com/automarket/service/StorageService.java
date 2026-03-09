package com.automarket.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over file storage backends.
 * Active implementation is chosen via {@code automarket.storage.provider} property:
 * - {@code local}: files saved to disk (dev/test)
 * - {@code s3}: files uploaded to AWS S3 with CloudFront (prod)
 */
public interface StorageService {

    /**
     * Stores a file and returns the storage result.
     *
     * @param file     the multipart file to store
     * @param folder   logical folder/prefix (e.g. "listings/uuid")
     * @return {@link UploadResult} containing the storage key and public URL
     */
    UploadResult store(MultipartFile file, String folder);

    /**
     * Deletes a previously stored file.
     *
     * @param storageKey the key returned by {@link #store}
     */
    void delete(String storageKey);

    record UploadResult(String storageKey, String url) {}
}
