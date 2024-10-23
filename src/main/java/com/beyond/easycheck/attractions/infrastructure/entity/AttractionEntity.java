package com.beyond.easycheck.attractions.infrastructure.entity;

import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
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

    @Column(nullable = false)
    private String name;

    private String introduction;

    private String information;

    private String standardUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_park_id", nullable = false)
    private ThemeParkEntity themePark;

    private String imageUrl;

    public static AttractionEntity createAttraction(String name, String introduction, String information, String standardUse, ThemeParkEntity themePark, String imageUrl) {
        return new AttractionEntity(null, name, introduction, information, standardUse, themePark, imageUrl);
    }

    public void update(String name, String introduction, String information, String standardUse) {
        this.name = name;
        this.introduction = introduction;
        this.information = information;
        this.standardUse = standardUse;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
