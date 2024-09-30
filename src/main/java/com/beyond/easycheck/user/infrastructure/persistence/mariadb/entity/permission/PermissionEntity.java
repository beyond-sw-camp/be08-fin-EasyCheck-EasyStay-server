package com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.permission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
}
