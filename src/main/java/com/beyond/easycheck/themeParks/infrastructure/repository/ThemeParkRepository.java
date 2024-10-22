package com.beyond.easycheck.themeParks.infrastructure.repository;

import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.EntityGraph;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemeParkRepository extends JpaRepository<ThemeParkEntity, Long> {

    @EntityGraph(attributePaths = {"accommodation"})
    List<ThemeParkEntity> findAllByAccommodation_Id(Long accommodationId);

    boolean existsByName(String name);

    List<ThemeParkEntity> findByAccommodationId(Long accommodationId);
}
