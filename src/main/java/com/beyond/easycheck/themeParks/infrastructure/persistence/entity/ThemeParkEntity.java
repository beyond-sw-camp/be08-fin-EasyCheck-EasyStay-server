package com.beyond.easycheck.themeparks.infrastructure.persistence.entity;


import com.beyond.easycheck.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ThemeParkEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
