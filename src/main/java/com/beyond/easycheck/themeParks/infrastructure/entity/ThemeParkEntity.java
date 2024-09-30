package com.beyond.easycheck.themeparks.infrastructure.entity;


import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.themeparks.application.service.ThemeParkOperationUseCase.ThemeParkCreateCommand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "theme_park")
public class ThemeParkEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_park_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    private String image;

    // 숙박 시설과의 연관관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private AccommodationEntity accommodation;

    public static ThemeParkEntity createThemePark(ThemeParkCreateCommand command, AccommodationEntity accommodation) {
        return new ThemeParkEntity(
                null,
                command.getName(),
                command.getDescription(),
                command.getLocation(),
                command.getImage(),
                accommodation
        );
    }

    public void update(String name, String description, String location, String image) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.image = image;
    }
}
