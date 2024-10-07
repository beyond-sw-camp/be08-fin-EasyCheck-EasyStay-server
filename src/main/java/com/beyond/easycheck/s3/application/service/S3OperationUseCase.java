package com.beyond.easycheck.s3.application.service;

import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3OperationUseCase {

    String uploadFile(MultipartFile file, FileManagementCategory category) throws IOException;

}
