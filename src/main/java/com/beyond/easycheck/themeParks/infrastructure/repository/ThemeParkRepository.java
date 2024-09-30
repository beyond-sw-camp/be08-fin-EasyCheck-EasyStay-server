package com.beyond.easycheck.themeparks.infrastructure.repository;

import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeParkRepository extends JpaRepository<ThemeParkEntity, Long> {

    boolean existsByNameAndLocation(String name, String location);
}
