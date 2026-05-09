package com.apartment.maintenance.controller;

import com.apartment.maintenance.service.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/view/{fileName}")
    public ResponseEntity<?> viewFile(@PathVariable String fileName)
            throws Exception {

        Path filePath = Path.of("uploads")
                .resolve(fileName)
                .normalize();

        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}