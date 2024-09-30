package com.beyond.easycheck.themeparks.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeparks.exception.ThemeParkMessageType;
import lombok.Builder;
import lombok.Getter;

public interface ThemeParkOperationUseCase {

    FindThemeParkResult saveThemePark(ThemeParkCreateCommand command, Long accommodationId);

    FindThemeParkResult updateThemePark(Long id, ThemeParkUpdateCommand command, Long accommodationId);

    void deleteThemePark(Long id, Long accommodationId);

    @Getter
    @Builder
    class ThemeParkCreateCommand {
        private String name;
        private String description;
        private String location;
        private String image;

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new EasyCheckException(ThemeParkMessageType.VALIDATION_FAILED);
            }
            if (location == null || location.trim().isEmpty()) {
                throw new EasyCheckException(ThemeParkMessageType.VALIDATION_FAILED);
            }
        }
    }

    @Getter
    @Builder
    class ThemeParkUpdateCommand {
        private final String name;
        private final String description;
        private final String location;
        private final String image;
    }
}
