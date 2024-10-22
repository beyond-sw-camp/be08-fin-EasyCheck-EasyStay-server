package com.beyond.easycheck.suggestion.infrastructure.persistence.repository;

import com.beyond.easycheck.suggestion.infrastructure.persistence.entity.SuggestionEntity;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SuggestionsRepository extends JpaRepository<SuggestionEntity, Long> {

    @EntityGraph(attributePaths = {"userEntity"})
    List<SuggestionEntity> findAllByAccommodationEntity_Id(Long accommodationId);

}
