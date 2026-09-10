package com.telehealth.screening.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.telehealth.screening.Exceptions.BadRequestException;

import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {

    private final Path storageDir;

    public ImageStorageService(@Value("${screening.image.storage-dir}") String dir) {
        this.storageDir = Path.of(dir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        validate(file);
        try {
            Files.createDirectories(storageDir);
            String ref = UUID.randomUUID() + getExtension(file);
            Files.write(storageDir.resolve(ref), file.getBytes());
            log.info("AUDIT: image stored, ref={}", ref);
            return ref;
        } catch (Exception e) {
            throw new RuntimeException("Image storage failed", e);
        }
    }

    public byte[] retrieve(String imageRef) {
        try {
            Path path = storageDir.resolve(imageRef).normalize();
            if (!path.startsWith(storageDir)) {
                throw new BadRequestException("Invalid image reference");
            }
            if (!Files.exists(path)) {
                throw new BadRequestException("Image not found: " + imageRef);
            }
            return Files.readAllBytes(path);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Image retrieval failed", e);
        }
    }

    public String calculateHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate image hash", e);
        }
    }

    private String getExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            return original.substring(original.lastIndexOf('.')).toLowerCase();
        }
        return ".jpg";
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is empty");
        }
        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) {
            throw new BadRequestException("Only image files allowed");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BadRequestException("Image too large (max 10MB)");
        }
    }
}