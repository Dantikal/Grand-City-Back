package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/avif",
            "application/pdf",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation");

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    @Value("${app.uploads.public-base-url}")
    private String publicBaseUrl;

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was uploaded (expected multipart field 'file')");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only images (jpeg, png, webp, gif, avif) and documents (pdf, ppt, pptx) are allowed");
        }

        try {
            Path directory = Path.of(uploadsDir);
            Files.createDirectories(directory);

            String original = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
            String extension = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                extension = original.substring(dot);
            }

            String filename = UUID.randomUUID() + extension;
            Path destination = directory.resolve(filename);
            file.transferTo(destination);

            String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
            return base + "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded file: " + e.getMessage(), e);
        }
    }
}
