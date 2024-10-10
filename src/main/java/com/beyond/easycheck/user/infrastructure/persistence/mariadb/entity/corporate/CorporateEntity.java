package com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.corporate;

import com.beyond.easycheck.corporate.application.CorporateOperationUseCase.CorporateCreateCommand;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "CORPORATE")
@ToString(of = {"id", "name", "businessLicenseNumber", "verificationZipPath"})
public class CorporateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String businessLicenseNumber;

    private String verificationZipPath;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private CorporateEntity(String name, String businessLicenseNumber) {
        this.name = name;
        this.businessLicenseNumber = businessLicenseNumber;
    }

    public static CorporateEntity createCorporate(CorporateCreateCommand command){
        return new CorporateEntity(
                command.name(),
                command.businessLicenseNumber()
        );
    }

    public void setVerificationZipPath(String verificationZipPath) {
        this.verificationZipPath = verificationZipPath;
    }

}


