package com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user;

import com.beyond.easycheck.user.application.service.UserOperationUseCase.UserRegisterCommand;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role.RoleEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role.UserPermissionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
@ToString(of = {"id", "email", "name", "phone", "addr", "addrDetail", "marketingConsent"})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String phone;

    private String addr;

    private String addrDetail;

    private String status;

    private char marketingConsent;

    private int point;

    @CreationTimestamp
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp updatedDate;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user")
    private List<UserPermissionEntity> userPermission;

    private UserEntity(String email, String name, String phone, String addr, String addrDetail, char marketingConsent) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.addr = addr;
        this.addrDetail = addrDetail;
        this.marketingConsent = marketingConsent;
    }

    public static UserEntity createUser(UserRegisterCommand command) {
        return new UserEntity(
                command.email(),
                command.name(),
                command.phone(),
                command.addr(),
                command.addrDetail(),
                command.marketingConsent()
        );
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public void setSecurePassword(String password) {
        this.password = password;
    }
}
