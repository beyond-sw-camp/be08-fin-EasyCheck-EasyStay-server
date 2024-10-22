package com.beyond.easycheck.themeParks.infrastructure.repository;

import com.beyond.easycheck.themeParks.infrastructure.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemeParkRepository extends JpaRepository<ThemeParkEntity, Long> {

    boolean existsByNameAndLocation(String name, String location);

    List<ThemeParkEntity> findByAccommodationId(Long accommodationId);
}
