package com.beyond.easycheck.infrastructure.persistence.repository;

import com.beyond.easycheck.infrastructure.persistence.entity.SuggestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionsRepository extends JpaRepository<SuggestionEntity, Long> {

}
