package com.beyond.easycheck.roomrates.ui.requestbody;

import com.beyond.easycheck.roomrates.infrastructure.entity.RoomrateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomrateUpdateRequest {

    @NotNull
    private Long roomEntity;

    @NotNull
    private Long seasonEntity;

    @NotBlank
    private RoomrateType rateType;

    @NotNull
    private BigDecimal rate;

}
