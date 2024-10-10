package com.beyond.easycheck.corporate.application;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.corporate.exception.CorporateMessageType;
import com.beyond.easycheck.s3.application.domain.FileManagementCategory;
import com.beyond.easycheck.s3.application.service.S3Service;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.corporate.CorporateEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.CorporateJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CorporateService implements CorporateOperationUseCase, CorporateReadUseCase {

    public static final int BUSINESS_LICENSE_LENGTH = 10;

    private final S3Service s3Service;

    private final CorporateJpaRepository corporateJpaRepository;

    @Override
    @Transactional
    public void createCorporate(CorporateCreateCommand command) {

        // 사업자 번호 양식 검증
        validateBusinessLicenseNumber(command.businessLicenseNumber());

        CorporateEntity corporateEntity = CorporateEntity.createCorporate(command);

        // 사업자 증명 압축 파일 S3에 업로드
        String verificationZipPath = s3Service.uploadFile(command.zipFile(), FileManagementCategory.USER);
        corporateEntity.setVerificationZipPath(verificationZipPath);

        corporateJpaRepository.save(corporateEntity);
    }

    private void validateBusinessLicenseNumber(String businessLicenseNumber) {

        if (BUSINESS_LICENSE_LENGTH != businessLicenseNumber.length()) {
            throw new EasyCheckException(CorporateMessageType.INVALID_BUSINESS_LICENSE_NUMBER);
        }

        try {
            Long.parseLong(businessLicenseNumber);
        } catch (NumberFormatException e) {
            throw new EasyCheckException(CorporateMessageType.INVALID_BUSINESS_LICENSE_NUMBER);
        }
    }
}
