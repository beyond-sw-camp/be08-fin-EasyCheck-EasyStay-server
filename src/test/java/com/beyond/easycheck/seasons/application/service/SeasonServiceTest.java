package com.beyond.easycheck.seasons.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.seasons.exception.SeasonMessageType;
import com.beyond.easycheck.seasons.infrastructure.entity.SeasonEntity;
import com.beyond.easycheck.seasons.infrastructure.repository.SeasonRepository;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonCreateRequest;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonUpdateRequest;
import com.beyond.easycheck.seasons.ui.view.SeasonView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.beyond.easycheck.seasons.exception.SeasonMessageType.SEASONS_NOT_FOUND;
import static com.beyond.easycheck.seasons.exception.SeasonMessageType.SEASON_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class SeasonServiceTest {

    @Mock
    private SeasonRepository seasonRepository;

    @InjectMocks
    private SeasonService seasonService;

    @Test
    @DisplayName("시즌 생성 성공")
    void createSeason_success() {
        // Given
        SeasonCreateRequest seasonCreateRequest = new SeasonCreateRequest(
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        SeasonEntity seasonEntity = new SeasonEntity(
                1L,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        when(seasonRepository.save(any(SeasonEntity.class))).thenReturn(seasonEntity);

        // When & Then
        assertThatCode(() -> seasonService.createSeason(seasonCreateRequest))
                .doesNotThrowAnyException();

        // Verify
        verify(seasonRepository).save(any(SeasonEntity.class));
    }

    @Test
    @DisplayName("시즌 생성 실패 - 잘못된 입력값")
    void createSeason_fail() {
        // Given
        SeasonCreateRequest seasonCreateRequest = new SeasonCreateRequest(
                null,
                "",
                null,
                null
        );

        // When & Then
        assertThatThrownBy(() -> seasonService.createSeason(seasonCreateRequest))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(SeasonMessageType.ARGUMENT_NOT_VALID.getMessage());

        // Verify
        verify(seasonRepository, never()).save(any(SeasonEntity.class));
    }

    @Test
    @DisplayName("시즌 단일 조회 성공")
    void readSeason_success() {
        // Given
        SeasonEntity seasonEntity = new SeasonEntity(
                1L,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(seasonEntity));


        SeasonView seasonView = new SeasonView(
                1L,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        // When
        SeasonView readSeason = seasonService.readSeason(1L);

        // Then
        assertThat(readSeason.getId()).isEqualTo(seasonView.getId());
        assertThat(readSeason.getSeasonName()).isEqualTo(seasonView.getSeasonName());
        assertThat(readSeason.getDescription()).isEqualTo(seasonView.getDescription());
        assertThat(readSeason.getStartDate()).isEqualTo(seasonView.getStartDate());
        assertThat(readSeason.getEndDate()).isEqualTo(seasonView.getEndDate());
    }

    @Test
    @DisplayName("시즌 단일 조회 실패 - 잘못된 seasonId")
    void readSeason_fail() {
        // Given
        Long seasonId = 999L;

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> seasonService.readSeason(seasonId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(SEASON_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("시즌 전체 조회 성공")
    void readSeasons_success() {
        SeasonEntity season1 = new SeasonEntity(
                1L,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        SeasonEntity season2 = new SeasonEntity(
                2L,
                "여름",
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        List<SeasonEntity> seasonEntities = Arrays.asList(season1, season2);
        when(seasonRepository.findAll()).thenReturn(seasonEntities);

        // When
        List<SeasonView> seasonViews = seasonService.readSeasons();

        // Then
        assertThat(seasonViews).hasSize(2);
        assertThat(seasonViews.get(0).getId()).isEqualTo(season1.getId());
        assertThat(seasonViews.get(1).getId()).isEqualTo(season2.getId());

        // Verify
        verify(seasonRepository).findAll();
    }

    @Test
    @DisplayName("시즌 전체 조회 실패 - 빈 리스트")
    void readSeasons_fail() {
        when(seasonRepository.findAll()).thenThrow(new EasyCheckException(SEASONS_NOT_FOUND));

        assertThatThrownBy(() -> seasonService.readSeasons())
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(SEASONS_NOT_FOUND.getMessage());

        // Verify
        verify(seasonRepository).findAll();
    }

    @Test
    @DisplayName("시즌 수정 성공")
    void updateSeason_success() {
        SeasonEntity seasonEntity = new SeasonEntity(
                1L,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        SeasonUpdateRequest seasonUpdateRequest = new SeasonUpdateRequest(
                "여름",
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(seasonEntity));

        // When
        seasonService.updateSeason(1L, seasonUpdateRequest);

        // Then
        assertThat(seasonEntity.getSeasonName()).isEqualTo("여름");
        assertThat(seasonEntity.getDescription()).isEqualTo("따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.");
        assertThat(seasonEntity.getStartDate()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(seasonEntity.getEndDate()).isEqualTo(LocalDate.of(2024, 8, 31));

        // Verify
        verify(seasonRepository).findById(1L);
    }

    @Test
    @DisplayName("시즌 수정 실패 - 잘못된 입력값")
    void updateSeason_fail() {
        SeasonEntity seasonEntity = new SeasonEntity(
                1L,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        SeasonUpdateRequest seasonUpdateRequest = new SeasonUpdateRequest(
                null,
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        when(seasonRepository.findById(1L)).thenReturn(Optional.of(seasonEntity));

        // When & Then
        assertThatThrownBy(() -> seasonService.updateSeason(1L, seasonUpdateRequest))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(SeasonMessageType.ARGUMENT_NOT_VALID.getMessage());

        // Verify
        verify(seasonRepository).findById(1L);
        verify(seasonRepository, never()).save(any(SeasonEntity.class));
    }

    @Test
    @DisplayName("시즌 수정 실패 - 잘못된 seasonId")
    void updateSeason_fail_wrongId() {
        // Given
        Long invalidSeasonId = 999L;

        SeasonUpdateRequest seasonUpdateRequest = new SeasonUpdateRequest(
                "여름",
                "따뜻한 여름철, 해수욕과 바캉스를 즐길 수 있는 시즌입니다.",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 8, 31)
        );

        // When & Then
        assertThatThrownBy(() -> seasonService.updateSeason(invalidSeasonId, seasonUpdateRequest))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(SEASON_NOT_FOUND.getMessage());

        // Verify
        verify(seasonRepository).findById(invalidSeasonId);
        verify(seasonRepository, never()).save(any(SeasonEntity.class));
    }

    @Test
    @DisplayName("시즌 삭제 성공")
    void deleteSeason_success() {
        // Given
        Long seasonId = 1L;

        SeasonEntity seasonEntity = new SeasonEntity(
                seasonId,
                "가을",
                "선선한 가을, 단풍놀이와 추수의 계절입니다.",
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 11, 30)
        );

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(seasonEntity));

        // When
        seasonService.deleteSeason(seasonId);

        // Then
        verify(seasonRepository).findById(seasonId);
        verify(seasonRepository).delete(seasonEntity);
    }

    @Test
    @DisplayName("시즌 삭제 실패 - 잘못된 seasonId")
    void deleteSeason_fail() {
        // Given
        Long invalidSeasonId = 999L;

        when(seasonRepository.findById(invalidSeasonId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> seasonService.deleteSeason(invalidSeasonId))
                .isInstanceOf(EasyCheckException.class)
                .hasMessage(SEASON_NOT_FOUND.getMessage());

        // Verify
        verify(seasonRepository).findById(invalidSeasonId);
        verify(seasonRepository, never()).delete(any(SeasonEntity.class));
    }

}


