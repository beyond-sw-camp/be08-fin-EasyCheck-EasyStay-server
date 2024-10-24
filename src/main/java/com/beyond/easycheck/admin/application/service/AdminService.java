package com.beyond.easycheck.admin.application.service;

import com.beyond.easycheck.additionalservices.infrastructure.repository.AdditionalServiceRepository;
import com.beyond.easycheck.admin.exception.AdminMessageType;
import com.beyond.easycheck.attractions.infrastructure.repository.AttractionRepository;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.events.infrastructure.repository.EventRepository;
import com.beyond.easycheck.facilities.infrastructure.repository.FacilityRepository;
import com.beyond.easycheck.notices.infrastructure.persistence.repository.NoticesRepository;
import com.beyond.easycheck.payments.infrastructure.repository.PaymentRepository;
import com.beyond.easycheck.suggestion.infrastructure.persistence.repository.SuggestionsRepository;
import com.beyond.easycheck.adasfas.infrastructure.repository.ThemeParkRepository;
import com.beyond.easycheck.user.exception.UserMessageType;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static com.beyond.easycheck.user.application.service.UserReadUseCase.*;
import static com.beyond.easycheck.user.application.service.UserReadUseCase.FindUserResult;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService implements AdminOperationUseCase, AdminReadUseCase {

    private final EventRepository eventRepository;

    private final UserJpaRepository userJpaRepository;

    private final NoticesRepository noticesRepository;

    private final PaymentRepository paymentRepository;

    private final FacilityRepository facilityRepository;

    private final ThemeParkRepository themeParkRepository;

    private final AttractionRepository attractionRepository;

    private final SuggestionsRepository suggestionsRepository;

    private final AdditionalServiceRepository additionalServiceRepository;

    @Override
    @Transactional
    public FindUserResult updateUserStatus(UserStatusUpdateCommand command) {

        UserEntity userEntity = userJpaRepository.findById(command.userId())
                .orElseThrow(() -> new EasyCheckException(UserMessageType.USER_NOT_FOUND));

        userEntity.setUserStatus(command.status());

        return FindUserResult.findByUserEntity(userEntity);
    }

    @Override
    public List<FindUserResult> getAllUsers(UserFindQuery query) {
        return List.of();
    }

    @Override
    public FindUserResult getUserDetails(UserFindQuery query) {
        return null;
    }

    @Override
    public FindAdminResult getAdminDetails() {
        return null;
    }

    @Override
    public List<FindSuggestionResult> getAllSuggestions() {
        log.info("[AdminService - getAllSuggestions]");
        return suggestionsRepository.findAllByAccommodationEntity_Id(getManagerAccommodationId())
                .stream()
                .map(FindSuggestionResult::findBySuggestionEntity)
                .toList();
    }

    @Override
    public List<FindFacilitiesResult> getAllFacilities() {
        return facilityRepository.findAllByAccommodationEntity_Id(getManagerAccommodationId())
                .stream()
                .map(FindFacilitiesResult::findByFacilityEntity)
                .toList();
    }

    @Override
    public List<FindAdditionalServiceResult> getAllAdditionalServices() {
        return additionalServiceRepository.findAllByAccommodationEntity_Id(getManagerAccommodationId())
                .stream()
                .map(FindAdditionalServiceResult::findByAdditionalServiceEntity)
                .toList();
    }

    @Override
    public List<FindNoticeResult> getAllNotices() {
        return noticesRepository.findAllByAccommodationEntity_Id(getManagerAccommodationId())
                .stream()
                .map(FindNoticeResult::findByNoticeWithUserAndAccommodation)
                .toList();
    }

    @Override
    public List<FindThemeParkResult> getAllThemeParks() {
        return themeParkRepository.findAllByAccommodation_Id(getManagerAccommodationId())
                .stream()
                .map(FindThemeParkResult::findByThemeParkEntity)
                .toList();
    }

    @Override
    public List<FindEventResult> getAllEvents() {
        return eventRepository.findAllByAccommodationEntity_Id(getManagerAccommodationId())
                .stream()
                .map(FindEventResult::findByEvent)
                .toList();
    }

    @Override
    public List<FindAttractionResult> getAllAttractions() {
        return attractionRepository
                .findAllByAccommodationId(getManagerAccommodationId())
                .stream()
                .map(FindAttractionResult::findByAttractionEntity)
                .toList();
    }

    @Override
    public List<FindPaymentResult> getAllPayments() {
        return paymentRepository.findAllByAccommodationId(getManagerAccommodationId())
                .stream()
                .map(FindPaymentResult::findByPaymentEntity)
                .toList();
    }

    public Long getManagerAccommodationId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            // ROLE_숫자_ADMIN 형식인지 확인
            if (role.matches("ROLE_\\d+_ADMIN")) {
                // ROLE_ 를 제거하고 _ADMIN 을 제거한 후 남은 숫자를 추출
                String idStr = role.replace("ROLE_", "")
                        .replace("_ADMIN", "");
                return Long.parseLong(idStr);
            }
        }

        // 해당하는 권한이 없을 경우 예외
        throw new EasyCheckException(AdminMessageType.ACCOMMODATION_ADMIN_AUTHORITY_NOT_FOUND);
    }
}
