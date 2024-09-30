package com.beyond.easycheck.seasons.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.seasons.exception.SeasonMessageType;
import com.beyond.easycheck.seasons.infrastructure.entity.SeasonEntity;
import com.beyond.easycheck.seasons.infrastructure.repository.SeasonRepository;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonCreateRequest;
import com.beyond.easycheck.seasons.ui.requestbody.SeasonUpdateRequest;
import com.beyond.easycheck.seasons.ui.view.SeasonView;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository seasonRepository;

    @Transactional
    public void createSeason(SeasonCreateRequest seasonCreateRequest) {

        SeasonEntity season = SeasonEntity.builder()
                .seasonName(seasonCreateRequest.getSeasonName())
                .description(seasonCreateRequest.getDescription())
                .startDate(seasonCreateRequest.getStartDate())
                .endDate(seasonCreateRequest.getEndDate())
                .build();

        seasonRepository.save(season);
    }

    @Transactional
    public SeasonView readSeason(Long seasonId) {

        SeasonEntity season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new EasyCheckException(SeasonMessageType.SEASON_NOT_FOUND));

        SeasonView seasonView = SeasonView.builder()
                .id(season.getId())
                .seasonName(season.getSeasonName())
                .description(season.getDescription())
                .startDate(season.getStartDate())
                .endDate(season.getEndDate())
                .build();

        return seasonView;
    }

    @Transactional
    public List<SeasonView> readSeasons() {

        List<SeasonEntity> seasonEntities = seasonRepository.findAll();

        if (seasonEntities.isEmpty()) {
            throw new EasyCheckException(SeasonMessageType.SEASON_NOT_FOUND);
        }
        List<SeasonView> seasonViews = seasonEntities.stream()
                .map(seasonEntity -> new SeasonView(
                        seasonEntity.getId(),
                        seasonEntity.getSeasonName(),
                        seasonEntity.getDescription(),
                        seasonEntity.getStartDate(),
                        seasonEntity.getEndDate()
                )).collect(Collectors.toList());

        return seasonViews;
    }

    @Transactional
    public void updateSeason(Long seasonId, SeasonUpdateRequest seasonUpdateRequest) {

        SeasonEntity season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new EasyCheckException(SeasonMessageType.SEASON_NOT_FOUND));

        season.update(seasonUpdateRequest);
    }

    @Transactional
    public void deleteSeason(Long seasonId) {

        SeasonEntity season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new EasyCheckException(SeasonMessageType.SEASON_NOT_FOUND));

        seasonRepository.delete(season);
    }

}
