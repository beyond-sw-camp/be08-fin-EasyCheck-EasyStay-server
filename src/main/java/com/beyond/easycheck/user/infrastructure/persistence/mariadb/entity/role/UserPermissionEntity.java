package com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role;


import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.permission.PermissionEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Timestamp grantedDatetime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private PermissionEntity permission;
}
