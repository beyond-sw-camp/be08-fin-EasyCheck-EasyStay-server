package com.beyond.easycheck.rooms.infrastructure.persistence.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_rate")
public class RoomRateEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long seasonId;

    @Column(nullable = false)
    private RateType rateType;

    @Column(nullable = false)
    private BigDecimal rate;




}
