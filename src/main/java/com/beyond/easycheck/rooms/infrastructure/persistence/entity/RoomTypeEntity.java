package com.beyond.easycheck.rooms.infrastructure.persistence.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "room_type")
public class RoomTypeEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;

    @Column(nullable = false)
    private Long accomodationId;

    @Column(nullable = false)
    private String typeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int max_occupancy;

}
