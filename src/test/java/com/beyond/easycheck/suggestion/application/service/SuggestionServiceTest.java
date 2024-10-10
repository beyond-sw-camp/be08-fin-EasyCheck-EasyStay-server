package com.beyond.easycheck.suggestion.application.service;

import com.beyond.easycheck.accomodations.exception.AccommodationMessageType;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.notices.exception.NoticesMessageType;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import com.beyond.easycheck.suggestion.exception.SuggestionMessageType;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.AgreementType;
import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.SuggestionEntity;
import com.beyond.easycheck.suggestion.ui.requestbody.SuggestionCreateRequest;
import com.beyond.easycheck.suggestion.ui.view.SuggestionView;
import com.beyond.easycheck.user.exception.UserMessageType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
@Transactional
class SuggestionServiceTest {

    @Autowired
    private SuggestionService suggestionService;

    @Test
    @DisplayName("[건의사항 등록] - 성공")
    void createSuggestion_success() {
        // given
        SuggestionCreateRequest request = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);

        // when
        Optional<SuggestionEntity> result = suggestionService.createSuggestion(1L, request);

        // then
        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get().getId()).isNotNull();
        Assertions.assertThat(result.get().getType()).isEqualTo(request.getType());
        Assertions.assertThat(result.get().getSubject()).isEqualTo(request.getSubject());
        Assertions.assertThat(result.get().getEmail()).isEqualTo(request.getEmail());
        Assertions.assertThat(result.get().getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(result.get().getContent()).isEqualTo(request.getContent());
        Assertions.assertThat(result.get().getAgreementType()).isEqualTo(request.getAgreementType());

    }

    @Test
    @DisplayName("[건의사항 등록] - 숙소 찾기 실패")
    void createSuggestion_fail_due_to_accommodation() {
        // given
        SuggestionCreateRequest request = new SuggestionCreateRequest(9999L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);

        // when & then
        Assertions.assertThatThrownBy(() -> {
            suggestionService.createSuggestion(1L, request);
        }).isInstanceOf(EasyCheckException.class)
                .hasMessage(AccommodationMessageType.ACCOMMODATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[건의사항 등록] - 관리자 찾기 실패")
    void CreateNotices_fail_due_to_user() {
        // given
        // 존재하는 숙소  ID로 요청 생성
        SuggestionCreateRequest request = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);

        // when & then
        Assertions.assertThatThrownBy(() -> {
                    suggestionService.createSuggestion(9999L, request);
                }).isInstanceOf(EasyCheckException.class)
                .hasMessage(UserMessageType.USER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("[건의사항 목록 조회] - 성공")
    void getAllsuggestions() {
        // given
        SuggestionCreateRequest request1 = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);
        SuggestionCreateRequest request2 = new SuggestionCreateRequest(1L, "편의시설", "칭찬", "enjoy2573@gmail.com", " 편의시설을 칭찬합니다.", "편의시설이 너무 깨끗하고 좋습니다. 다음에 또 오고 싶어요.", "", AgreementType.Agree);

        suggestionService.createSuggestion(1L, request1);
        suggestionService.createSuggestion(1L, request2);

        // when
        List<SuggestionView> result = suggestionService.getAllsuggestions(0,10);

        // then
        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo(request1.getTitle());
        Assertions.assertThat(result.get(1).getTitle()).isEqualTo(request2.getTitle());


    }

    @Test
    @DisplayName("[건의사항 목록 조회] - 빈 리스트")
    void getAllsuggestions_emptyList() {
        // when
        List<SuggestionView> result = suggestionService.getAllsuggestions(0,10);

        // then
        Assertions.assertThat(result).isEmpty(); // 결과는 빈리스트여야 함.
    }

    @Test
    @DisplayName("[건의사항 조회] - 성공")
    void getSuggestionById() {
        // given
        SuggestionCreateRequest request = new SuggestionCreateRequest(1L, "객실", "문의", "enjoy2573@gmail.com", "객실 관련 문의합니다.", "객실 사용 후 휴대폰을 분실하였습니다.", "", AgreementType.Agree);
        Optional<SuggestionEntity> createSuggestion = suggestionService.createSuggestion(1L, request);

        Assertions.assertThat(createSuggestion).isPresent();

        Long suggestionId = createSuggestion.get().getId();

        // when
         suggestionService.createSuggestion(suggestionId, request);

        // then
        SuggestionView suggestionView = suggestionService.getSuggestionById(suggestionId);
        Assertions.assertThat(suggestionView).isNotNull();
        Assertions.assertThat(suggestionView.getType()).isEqualTo(request.getType());
        Assertions.assertThat(suggestionView.getSubject()).isEqualTo(request.getSubject());
        Assertions.assertThat(suggestionView.getEmail()).isEqualTo(request.getEmail());
        Assertions.assertThat(suggestionView.getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(suggestionView.getContent()).isEqualTo(request.getContent());
        Assertions.assertThat(suggestionView.getAgreementType()).isEqualTo(request.getAgreementType());

    }

    @Test
    @DisplayName("[건의사항 조회] - 건의사항 찾기 실패")
    void getSuggestion_fail(){
        // given
        Long invalidId = 9999L;

        // when & then
        Assertions.assertThatThrownBy(() -> suggestionService.getSuggestionById(invalidId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(SuggestionMessageType.SUGGESTION_NOT_FOUND.getMessage());
    }

    @Test
    void getSuggestionEntity() {
    }

    @Test
    void replySuggestion() {

    }


}