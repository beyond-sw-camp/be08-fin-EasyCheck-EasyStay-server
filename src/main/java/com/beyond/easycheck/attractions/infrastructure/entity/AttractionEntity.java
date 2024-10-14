package com.beyond.easycheck.attractions.infrastructure.entity;

import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
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

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_park_id", nullable = false)
    private ThemeParkEntity themePark;

    @OneToMany(mappedBy = "attraction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> images = new ArrayList<>();

    public static AttractionEntity createAttraction(String name, String description, ThemeParkEntity themePark) {
        return new AttractionEntity(null, name, description, themePark, new ArrayList<>());
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
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
