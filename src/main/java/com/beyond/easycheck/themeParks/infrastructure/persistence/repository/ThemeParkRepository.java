package com.beyond.easycheck.themeparks.infrastructure.persistence.repository;

import com.beyond.easycheck.themeparks.infrastructure.persistence.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeParkRepository extends JpaRepository<ThemeParkEntity, Long> {

    boolean existsByNameAndLocation(String name, String location);
}
