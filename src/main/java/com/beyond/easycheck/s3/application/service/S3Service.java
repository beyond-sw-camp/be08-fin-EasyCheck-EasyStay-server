package com.beyond.easycheck.s3.application.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service implements S3OperationUseCase, S3ReadUseCase {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3client;

    public String uploadFile(MultipartFile file, FileManagementCategory category) {
        String fileName = null;
        try {
            File convertedFile = convertMultiPartToFile(file);
            fileName = generateFileName(file, category);
            s3client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
            convertedFile.delete();
        } catch (IOException e) {
            log.error("Error occurred while converting multipart file", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
        return s3client.getUrl(bucketName, fileName).toString();
    }

    public List<String> uploadFiles(List<MultipartFile> files, FileManagementCategory category) {
        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileUrl = uploadFile(file, category);
            uploadedUrls.add(fileUrl);
        }
        return uploadedUrls;
    }

    public void deleteFile(String fileUrl) {
        s3client.deleteObject(bucketName, fileUrl);
    }

    public void deleteFiles(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            String fileName = extractFileNameFromUrl(fileUrl);
            String[] parts = fileName.split("/");
            String deleteImageUrl = String.join("/", Arrays.copyOfRange(parts, 3, parts.length));
            s3client.deleteObject(bucketName, deleteImageUrl);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private String generateFileName(MultipartFile multiPart, FileManagementCategory category) {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String randomUUID = UUID.randomUUID().toString();
        String originalFileName = Objects.requireNonNull(multiPart.getOriginalFilename()).replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileExtension = getFileExtension(originalFileName);

        return String.format("%s/%s-%s-%s%s",
                category.getFolderName(),
                timeStamp,
                randomUUID.substring(0, 8),
                originalFileName.substring(0, Math.min(originalFileName.length(), 50)),
                fileExtension);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}