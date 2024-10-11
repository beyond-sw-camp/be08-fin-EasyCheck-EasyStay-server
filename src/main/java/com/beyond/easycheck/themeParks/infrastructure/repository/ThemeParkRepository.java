package com.beyond.easycheck.themeparks.infrastructure.repository;

import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemeParkRepository extends JpaRepository<ThemeParkEntity, Long> {

    boolean existsByNameAndLocation(String name, String location);

    List<ThemeParkEntity> findByAccommodationId(Long accommodationId);
}
