package com.apartment.maintenance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            String originalFileName =
                    file.getOriginalFilename() == null
                            ? "file"
                            : file.getOriginalFilename();

            String fileExtension = "";

            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex >= 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            String fileName =
                    "uploads/"
                            + UUID.randomUUID()
                            + fileExtension;

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    public S3File getFile(String fileName) {

        GetObjectRequest request =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();

        ResponseBytes<GetObjectResponse> responseBytes =
                s3Client.getObjectAsBytes(request);

        String contentType =
                responseBytes.response().contentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        return new S3File(
                responseBytes.asByteArray(),
                contentType,
                getDisplayName(fileName)
        );
    }

    private String getDisplayName(String fileName) {
        if (fileName == null) {
            return "file";
        }

        int slashIndex = fileName.lastIndexOf("/");
        if (slashIndex >= 0 && slashIndex < fileName.length() - 1) {
            return fileName.substring(slashIndex + 1);
        }

        return fileName;
    }

    public record S3File(
            byte[] content,
            String contentType,
            String fileName
    ) {
    }
}