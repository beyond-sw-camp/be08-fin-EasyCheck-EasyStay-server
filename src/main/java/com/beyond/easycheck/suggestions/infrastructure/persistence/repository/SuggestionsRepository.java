package com.beyond.easycheck.suggestions.infrastructure.persistence.repository;

import com.beyond.easycheck.suggestions.infrastructure.persistence.entity.SuggestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SuggestionsRepository extends JpaRepository<SuggestionEntity, Long> {

}
