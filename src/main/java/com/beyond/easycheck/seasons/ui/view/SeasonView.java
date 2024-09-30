package com.beyond.easycheck.seasons.ui.view;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@ToString
public class SeasonView {

    private Long id;

    private String seasonName;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    public SeasonView(Long id, String seasonName, String description, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.seasonName = seasonName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
