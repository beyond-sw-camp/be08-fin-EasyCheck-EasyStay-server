package com.beyond.easycheck.attractions.infrastructure.entity;

import com.beyond.easycheck.attractions.application.service.AttractionOperationUseCase.AttractionCreateCommand;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attraction")
public class AttractionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attraction_id")
    private Long id;

    private String name;
    private String description;
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_park_id", nullable = false)
    private ThemeParkEntity themePark;

    public static AttractionEntity createAttraction(AttractionCreateCommand command, ThemeParkEntity themePark) {
        return new AttractionEntity(
                null,
                command.getName(),
                command.getDescription(),
                command.getImage(),
                themePark
        );
    }

    public void update(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }
}