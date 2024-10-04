package com.beyond.easycheck.permissions.infrastructure.persistence.mariadb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "permission")
@ToString(of = {"id", "name", "description"})
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private PermissionEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static PermissionEntity createPermissionEntity(String name, String description) {
        return new PermissionEntity(name, description);
    }

}
