package com.beyond.easycheck.themeparks.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.application.service.ThemeParkReadUseCase.FindThemeParkResult;
import com.beyond.easycheck.themeparks.exception.ThemeParkMessageType;
import lombok.Builder;
import lombok.Getter;

public interface ThemeParkOperationUseCase {

    FindThemeParkResult saveThemePark(ThemeParkCreateCommand command);

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
}
