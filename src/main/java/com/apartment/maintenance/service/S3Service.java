package com.apartment.maintenance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public String uploadFile(
            MultipartFile file,
            String folder
    ) {
        try {
            String originalFileName =
                    file.getOriginalFilename() == null
                            ? "file"
                            : file.getOriginalFilename();

            String fileExtension = "";

            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex >= 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            String key =
                    folder
                            + "/"
                            + UUID.randomUUID()
                            + fileExtension;

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            return "https://"
                    + bucketName
                    + ".s3."
                    + region
                    + ".amazonaws.com/"
                    + key;

        } catch (IOException e) {
            throw new RuntimeException("Unable to upload file to S3", e);
        }
    }
}