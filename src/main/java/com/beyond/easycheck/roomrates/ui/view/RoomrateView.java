package com.beyond.easycheck.roomrates.ui.view;

import com.beyond.easycheck.roomrates.infrastructure.entity.RoomrateType;
import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
public class RoomrateView {

    private Long id;

    private RoomrateType rateType;

    private BigDecimal rate;

    private RoomStatus status;

    private String typeName;

    private String seasonName;

    public RoomrateView(Long id, RoomrateType rateType, BigDecimal rate, RoomStatus status, String typeName, String seasonName) {
        this.id = id;
        this.rateType = rateType;
        this.rate = rate;
        this.status = status;
        this.typeName = typeName;
        this.seasonName = seasonName;
    }
}
