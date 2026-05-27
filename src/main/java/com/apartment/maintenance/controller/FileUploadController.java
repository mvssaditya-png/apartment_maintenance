package com.apartment.maintenance.controller;

import com.apartment.maintenance.service.FileUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file) {

        String fileName = fileUploadService.uploadFile(file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        response.put("fileName", fileName);
        response.put("fileUrl", "/api/files/view/" + fileName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/view/**")
    public ResponseEntity<?> viewFile(
            jakarta.servlet.http.HttpServletRequest request
    ) throws Exception {

        String fullPath =
                request.getRequestURI()
                        .replace("/api/files/view/", "");

        Path basePath = Path.of("uploads").toAbsolutePath().normalize();

        Path filePath = basePath
                .resolve(fullPath)
                .normalize();

        if (!filePath.startsWith(basePath)) {
            throw new RuntimeException("Invalid file path");
        }

        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        String contentType = Files.probeContentType(filePath);

        if (contentType == null) {
            if (resource.getFilename() != null
                    && resource.getFilename().toLowerCase().endsWith(".pdf")) {
                contentType = "application/pdf";
            } else {
                contentType = "application/octet-stream";
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }

}