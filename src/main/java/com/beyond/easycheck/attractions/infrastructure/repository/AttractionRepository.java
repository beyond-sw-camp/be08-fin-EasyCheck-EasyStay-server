package com.beyond.easycheck.attractions.infrastructure.repository;

import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttractionRepository extends JpaRepository<AttractionEntity, Long> {

    List<AttractionEntity> findByThemeParkId(Long themeParkId);

    @Query("SELECT DISTINCT a FROM AttractionEntity a " +
            "JOIN FETCH a.themePark tp " +
            "JOIN FETCH tp.accommodation acc " +
            "WHERE acc.id = :accommodationId")
    List<AttractionEntity> findAllByAccommodationId(@Param("accommodationId") Long accommodationId);
}
