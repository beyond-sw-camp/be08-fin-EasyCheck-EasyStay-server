package com.beyond.easycheck.themeparks.infrastructure.persistence.repository;

import com.beyond.easycheck.themeparks.infrastructure.persistence.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemeParkRepository extends JpaRepository<ThemeParkEntity, Long> {

    List<ThemeParkEntity> findAll();
}
