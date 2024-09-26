package com.beyond.easycheck.room.infrastructure.persistence.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_rate")
public class RoomRateEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;

    @ManyToOne
    private RoomEntity roomId;

    @ManyToOne
    private SeasonEntity seasonId;

    @Column(nullable = false)
    private RateType rateType;

    @Column(nullable = false)
    private BigDecimal rate;

}
