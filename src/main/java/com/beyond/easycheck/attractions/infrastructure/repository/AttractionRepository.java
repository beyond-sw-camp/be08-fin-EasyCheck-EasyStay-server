package com.beyond.easycheck.attractions.infrastructure.repository;

import com.beyond.easycheck.attractions.infrastructure.entity.AttractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttractionRepository extends JpaRepository<AttractionEntity, Long> {

    List<AttractionEntity> findByThemeParkId(Long themeParkId);
}
