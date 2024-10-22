package com.beyond.easycheck.themeParks.application.service;

import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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

        private final String ticketAvailable;

        private final List<String> imageUrls;


        public static FindThemeParkResult findByThemeParkEntity(ThemeParkEntity themePark){

            List<String> imageUrls = themePark.getImages().stream()
                    .map(ThemeParkEntity.ImageEntity::getUrl)
                    .collect(Collectors.toList());

            return FindThemeParkResult.builder()
                    .id(themePark.getId())
                    .name(themePark.getName())
                    .description(themePark.getDescription())
                    .ticketAvailable(themePark.getTicketAvailable())
                    .imageUrls(imageUrls)
                    .build();
        }
    }
}
