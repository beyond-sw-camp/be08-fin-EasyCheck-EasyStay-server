package com.beyond.easycheck.themeparks.infrastructure.persistence.entity;


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

//    @ManyToOne
//    @JoinColumn
//    private int accomodationId;

    public static ThemeParkEntity createThemePark(ThemeParkCreateCommand command) {
        return new ThemeParkEntity(
                null,
                command.getName(),
                command.getDescription(),
                command.getLocation(),
                command.getImage()
        );
    }
}
