package com.beyond.easycheck.admin.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.events.infrastructure.entity.EventEntity;
import com.beyond.easycheck.facilities.infrastructure.entity.AvailableStatus;
import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.SuggestionEntity;
import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.user.application.service.UserReadUseCase.FindUserResult;
import com.beyond.easycheck.user.application.service.UserReadUseCase.UserFindQuery;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface AdminReadUseCase {

    List<FindUserResult> getAllUsers(UserFindQuery query);

    // 단일 유저 조회
    FindUserResult getUserDetails(UserFindQuery query);

    // 현재 로그인한 관리자 정보 조회
    FindAdminResult getAdminDetails();

    List<FindEventResult> getAllEvents();

    List<FindNoticeResult> getAllNotices();

    List<FindThemeParkResult> getAllThemeParks();

    List<FindFacilitiesResult> getAllFacilities();

    List<FindSuggestionResult> getAllSuggestions();

    List<FindAdditionalServiceResult> getAllAdditionalServices();

    record FindNoticeResult(
            Long id,
            String accommodationName,
            String userName,
            String title,
            String content
    ) {
        public static FindNoticeResult findByNoticeWithUserAndAccommodation(NoticesEntity notices) {
            return new FindNoticeResult(
                    notices.getId(),
                    notices.getAccommodationEntity().getName(),
                    notices.getUserEntity().getName(),
                    notices.getTitle(),
                    notices.getContent()
            );
        }
    }

    record FindThemeParkResult(
            Long id,
            String name,
            String description,
            String location,
            List<String> images
    ) {
        static public FindThemeParkResult findByThemeParkEntity(ThemeParkEntity themePark) {
            return new FindThemeParkResult(
                    themePark.getId(),
                    themePark.getName(),
                    themePark.getDescription(),
                    themePark.getLocation(),
                    themePark.getImages().stream().map(ThemeParkEntity.ImageEntity::getUrl).toList(
                    ));
        }
    }

    record FindEventResult(
            Long id,
            String accommodationName,
            List<String> images,
            String eventName,
            String detail,
            LocalDate startDate,
            LocalDate endDate

    ) {
        public static FindEventResult findByEvent(EventEntity event) {
            return new FindEventResult(
                    event.getId(),
                    event.getAccommodationEntity().getName(),
                    event.getImages().stream().map(EventEntity.ImageEntity::getUrl).toList(),
                    event.getEventName(),
                    event.getDetail(),
                    event.getStartDate(),
                    event.getEndDate()
            );
        }
    }

    record FindSuggestionResult(
            Long id,
            String type,
            String subject,
            String email,
            String title,
            String content,
            FindUserResult user
    ) {
        public static FindSuggestionResult findBySuggestionEntity(SuggestionEntity suggestion) {
            return new FindSuggestionResult(
                    suggestion.getId(),
                    suggestion.getType(),
                    suggestion.getSubject(),
                    suggestion.getEmail(),
                    suggestion.getTitle(),
                    suggestion.getContent(),
                    suggestion.getUserEntity() == null ?
                            null : FindUserResult.findByUserEntity(suggestion.getUserEntity()
                    )
            );
        }


    }

    record FindFacilitiesResult(
            Long id,
            String name,
            String description,
            List<String> images,
            String accommodationName,
            AvailableStatus availableStatus
    ) {
        static public FindFacilitiesResult findByFacilityEntity(FacilityEntity facility) {
            return new FindFacilitiesResult(
                    facility.getId(),
                    facility.getName(),
                    facility.getDescription(),
                    facility.getImages().stream().map(FacilityEntity.ImageEntity::getUrl).toList(),
                    facility.getAccommodationEntity().getName(),
                    facility.getAvailableStatus()
            );
        }
    }

    record FindAdditionalServiceResult(
            Long id,
            String name,
            String description,
            Integer price
    ) {
        public static FindAdditionalServiceResult findByAdditionalServiceEntity(AdditionalServiceEntity additionalService) {
            return new FindAdditionalServiceResult(
                    additionalService.getId(),
                    additionalService.getName(),
                    additionalService.getDescription(),
                    additionalService.getPrice()
            );
        }
    }

    record FindAdminResult() {
    }

}
