package com.beyond.easycheck.attractions.infrastructure.entity;

import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "attraction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> images = new ArrayList<>();

    public static AttractionEntity createAttraction(String name, String introduction, String information, String standardUse, ThemeParkEntity themePark) {
        return new AttractionEntity(null, name, introduction, information, standardUse, themePark, new ArrayList<>());
    }

    public void update(String name, String introduction, String information, String standardUse) {
        this.name = name;
        this.introduction = introduction;
        this.information = information;
        this.standardUse = standardUse;
    }

    public void addImage(ImageEntity imageEntity) {
        this.images.add(imageEntity);
        imageEntity.setAttraction(this);
    }

    public List<ImageEntity> getImages() {
        return images != null ? images : new ArrayList<>();
    }

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "attraction_image")
    public static class ImageEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "image_id")
        private Long id;

        @Column(nullable = false)
        private String url;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "attraction_id", nullable = false)
        private AttractionEntity attraction;

        public static ImageEntity createImage(String url, AttractionEntity attraction) {
            return new ImageEntity(null, url, attraction);
        }
    }
}
