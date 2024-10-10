package com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "role")
@ToString(of = {"id", "name"})
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
