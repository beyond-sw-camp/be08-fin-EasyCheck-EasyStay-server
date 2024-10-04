package com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity;


import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "user_permission")
public class UserPermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String grantedBy;

    @CreationTimestamp
    private Timestamp grantedDatetime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private PermissionEntity permission;

    private UserPermissionEntity(String grantedBy, UserEntity user, PermissionEntity permission) {
        this.grantedBy = grantedBy;
        this.user = user;
        this.permission = permission;
    }

    public static UserPermissionEntity grantPermission(String grantedBy, UserEntity user, PermissionEntity permission) {
        return new UserPermissionEntity(grantedBy, user, permission);
    }
}
