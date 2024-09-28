package com.beyond.easycheck.themeParks.infrastructure.persistence.repository;

import com.beyond.easycheck.themeParks.infrastructure.persistence.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeParkRepository extends JpaRepository<ThemeParkEntity, Long> {

    boolean existsByNameAndLocation(String name, String location);
}
