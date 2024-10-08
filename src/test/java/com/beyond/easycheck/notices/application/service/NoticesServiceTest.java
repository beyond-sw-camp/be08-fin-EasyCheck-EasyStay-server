package com.beyond.easycheck.notices.application.service;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.notices.exception.NoticesMessageType;
import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import com.beyond.easycheck.notices.ui.requestbody.NoticesCreateRequest;
import com.beyond.easycheck.notices.ui.requestbody.NoticesUpdateRequest;
import com.beyond.easycheck.notices.ui.view.NoticesView;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
@Transactional
class NoticesServiceTest {


    @Autowired
    private NoticesService noticesService;



    @Test
    @DisplayName("[공지사항 등록] - 성공")
    void CreateNotices_success() {
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L,"공지사항","공지사항 내용");

        // when
        Optional<NoticesEntity> result = noticesService.createNotices(4L, request);

        // then
        Assertions.assertThat(result.isPresent()).isTrue();
        // isPresent()가 True일 경우에만 아래 테스트가 진행됨.
        Assertions.assertThat(result.get().getId()).isNotNull();
        Assertions.assertThat(result.get().getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(result.get().getContent()).isEqualTo(request.getContent());

    }

    @Test
    @DisplayName("[공지사항 등록] - 숙소 찾기 실패")
    void CreateNotices_fail_due_to_accommodation() {
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(9999L,"공지사항","공지사항 내용");

        // when & then
        Assertions.assertThatThrownBy(() -> {
            noticesService.createNotices(4L, request);
        }).isInstanceOf(EasyCheckException.class)
                .hasMessage(NoticesMessageType.NOTICES_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("[공지사항 등록] - 관리자 찾기 실패")
    void CreateNotices_fail_due_to_user() {
        // given
        // 존재하는 숙소  ID로 요청 생성
        NoticesCreateRequest request = new NoticesCreateRequest(1L, "공지사항", "공지사항 내용");

        // when & then
        Assertions.assertThatThrownBy(() -> {
            noticesService.createNotices(9999L, request);
        }).isInstanceOf(EasyCheckException.class)
                .hasMessage(NoticesMessageType.NOTICES_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("[공지사항 조회] - 성공")
    void getAllNotices_success() {
        // given
        NoticesCreateRequest request1 = new NoticesCreateRequest(4L, "공지사항","공지사항 내용");
        NoticesCreateRequest request2 = new NoticesCreateRequest(4L, "공지사항 등록 중","공지사항 내용");

        noticesService.createNotices(4L, request1);
        noticesService.createNotices(4L, request2);

        // when
        List<NoticesView> result = noticesService.getAllNotices(0, 10);


        // then
        Assertions.assertThat(result).isNotEmpty(); // 결과가 비어있지 않다면
        Assertions.assertThat(result.size()).isEqualTo(2); // 두 개의 공지사항이 등록되었으므로
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo(request1.getTitle());
        Assertions.assertThat(result.get(1).getTitle()).isEqualTo(request2.getTitle());

    }

    @Test
    @DisplayName("[공지사항 목록 조회] - 빈 리스트")
    void getAllNotices_emptyList(){
        // when
        List<NoticesView> result = noticesService.getAllNotices(0, 10);

        // then
        Assertions.assertThat(result).isEmpty(); // 결과는 빈리스트여야 한다.
    }


    @Test
    @DisplayName("[공지사항 조회] - 성공")
    void getNotices_success(){
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L, "공지사항", "공지사항 내용");
        Optional<NoticesEntity> createNotice = noticesService.createNotices(4L, request);

        Assertions.assertThat(createNotice).isPresent();

        Long noticeId = createNotice.get().getId();

        // when
        noticesService.createNotices(noticeId, request);

        // then
        NoticesView noticesView = noticesService.getNotices(noticeId);
        Assertions.assertThat(noticesView).isNotNull();
        Assertions.assertThat(noticesView.getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(noticesView.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("[공지사항 조회] - 공지사항 찾기 실패")
    void getNotices_fail(){
        // given
        Long invalidId = 9999L;

        // when & then
        Assertions.assertThatThrownBy(() -> noticesService.getNotices(invalidId) )
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(NoticesMessageType.NOTICES_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("[공지사항 수정] - 성공")
    void updateNotices_success() {
        // given
        NoticesCreateRequest createrequest = new NoticesCreateRequest(1L, "공지사항", "공지사항 내용");
        Optional<NoticesEntity> createNotice = noticesService.createNotices(4L, createrequest);

        Assertions.assertThat(createNotice).isPresent();

        Long noticeId = createNotice.get().getId();

        NoticesUpdateRequest updateRequest = new NoticesUpdateRequest("수정된 공지사항 제목","수정한 내용");

        // when
        noticesService.updateNotices(noticeId, updateRequest);

        // then
        NoticesView updatedNotices = noticesService.getNotices(noticeId);
        Assertions.assertThat(updatedNotices).isNotNull();
        Assertions.assertThat(updatedNotices.getTitle()).isEqualTo(updateRequest.getTitle());
        Assertions.assertThat(updatedNotices.getContent()).isEqualTo(updateRequest.getContent());

    }

    @Test
    @DisplayName("[공지사항 수정] - 공지사항 찾기 실패")
    void updateNotices_fail(){

        // given
        Long invalidId = 9999L;
        NoticesUpdateRequest updateRequest = new NoticesUpdateRequest("수정된 공지사항","수정된 내용");

        // when & then
        Assertions.assertThatThrownBy(() -> noticesService.updateNotices(invalidId, updateRequest))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(NoticesMessageType.NOTICES_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("[공지사항 삭제] - 성공")
    void deleteNotices_success() {
        // given
        NoticesCreateRequest request = new NoticesCreateRequest(1L, "공지사항", "공지사항 내용");
        Optional<NoticesEntity> createNotice = noticesService.createNotices(4L, request);

        Assertions.assertThat(createNotice).isPresent();

        Long noticeId = createNotice.get().getId();

        // when
        noticesService.deleteNotices(noticeId);

        // then
        Assertions.assertThatThrownBy(() -> noticesService.getNotices(noticeId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(NoticesMessageType.NOTICES_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("[공지사항 삭제] - 공지사항 찾기 실패")
    void deleteNotices_fail(){
        // given
        Long invalidId = 9999L;

        // when & then
        Assertions.assertThatThrownBy(() -> noticesService.deleteNotices(invalidId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(NoticesMessageType.NOTICES_NOT_FOUND.getMessage());
    }

}