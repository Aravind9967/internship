package com.screenings.cervical_screenings.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${storage.images.base-path:./secure-images}")
    private String basePath;

    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        Path base = Paths.get(basePath).toAbsolutePath().normalize();
        if (!Files.exists(base)) {
            Files.createDirectories(base);
        }

        String originalName = file.getOriginalFilename();
        String extension = ".jpg";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
        }

        String fileName = UUID.randomUUID().toString() + extension;
        Path fullPath = base.resolve(fileName);

        Files.write(fullPath, file.getBytes(), StandardOpenOption.CREATE_NEW);

        return "enc:" + fileName;
    }

    public byte[] loadImage(String imageRef) throws IOException {
        if (imageRef == null || !imageRef.startsWith("enc:")) {
            throw new IllegalArgumentException("Invalid image reference");
        }

        String fileName = imageRef.substring(4); // remove "enc:"

        // Convert to absolute path
        Path base = Paths.get(basePath).toAbsolutePath().normalize();
        Path fullPath = base.resolve(fileName).normalize();

        System.out.println("Base path   : " + base);
        System.out.println("Full path   : " + fullPath);
        System.out.println("File exists : " + Files.exists(fullPath));

        // Security check (fixed for Windows)
        if (!fullPath.startsWith(base)) {
            throw new IllegalArgumentException("Invalid image path");
        }

        if (!Files.exists(fullPath)) {
            throw new IOException("Image file not found: " + fullPath);
        }

        return Files.readAllBytes(fullPath);
    }

    public String getContentType(String imageRef) {
        if (imageRef == null) return "application/octet-stream";

        String lower = imageRef.toLowerCase();
        if (lower.endsWith(".png"))  return "image/png";
        if (lower.endsWith(".gif"))  return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".bmp"))  return "image/bmp";
        return "image/jpeg";
    }
}