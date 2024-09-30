package com.beyond.easycheck.roomrates.ui.requestbody;

import com.beyond.easycheck.roomrates.infrastructure.entity.RoomrateType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RoomrateCreateRequest {

    @NotNull
    private Long roomEntity;

    @NotNull
    private Long seasonEntity;

    @NotNull
    private RoomrateType rateType;

    @NotNull
    private BigDecimal rate;
}
