package com.apartment.maintenance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() throws IOException {

        uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(uploadPath);
    }

    public String uploadFile(MultipartFile file) {

        try {

            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            String fileName =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path targetLocation = uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}