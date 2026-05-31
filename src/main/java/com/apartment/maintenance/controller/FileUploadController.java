package com.apartment.maintenance.controller;

import com.apartment.maintenance.service.FileUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
            @RequestParam("file") MultipartFile file
    ) {
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

        fullPath = URLDecoder.decode(
                fullPath,
                StandardCharsets.UTF_8
        );

        if (fullPath.contains("..")) {
            throw new RuntimeException("Invalid file path");
        }

        FileUploadService.S3File s3File =
                fileUploadService.getFile(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(s3File.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + s3File.fileName() + "\""
                )
                .body(s3File.content());
    }
}