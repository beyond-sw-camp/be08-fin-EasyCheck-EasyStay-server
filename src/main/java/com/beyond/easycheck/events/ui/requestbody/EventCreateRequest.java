package com.beyond.easycheck.events.ui.requestbody;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@ToString
public class EventCreateRequest {

    @NotNull
    private Long accommodationEntity;

    @NotBlank
    private String eventName;

    @NotBlank
    private String image;

    @NotBlank
    private String detail;

    @NotNull(message = "이벤트 시작 날짜를 지정해야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "이벤트 종료 날짜를 지정해야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
