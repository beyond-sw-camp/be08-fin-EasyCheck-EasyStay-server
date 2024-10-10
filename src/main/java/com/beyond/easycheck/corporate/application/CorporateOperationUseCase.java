package com.beyond.easycheck.corporate.application;

import org.springframework.web.multipart.MultipartFile;

public interface CorporateOperationUseCase {

    void createCorporate(CorporateCreateCommand command);

    record CorporateCreateCommand(
            Long userId,
            String name,
            String businessLicenseNumber,
            MultipartFile zipFile
    ) {

    }
}
