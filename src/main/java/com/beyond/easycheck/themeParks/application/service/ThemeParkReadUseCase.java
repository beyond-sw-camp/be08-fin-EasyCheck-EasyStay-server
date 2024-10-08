package com.beyond.easycheck.themeparks.application.service;

import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

public interface ThemeParkReadUseCase {

    List<FindThemeParkResult> getThemeParks(Long accommodationId);

    FindThemeParkResult getFindThemePark(Long id, Long accommodationId);

    @Getter
    @Builder
    @EqualsAndHashCode
    class FindThemeParkResult {
        private final Long id;

        private final String name;

        private final String description;

        private final String location;

        private final String image;

        public static FindThemeParkResult findByThemeParkEntity(ThemeParkEntity themePark){
            return FindThemeParkResult.builder()
                    .id(themePark.getId())
                    .name(themePark.getName())
                    .description(themePark.getDescription())
                    .location(themePark.getLocation())
                    .image(themePark.getImage())
                    .build();
        }
    }
}
