package com.beyond.easycheck.themeparks.infrastructure.repository;

import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ThemeParkImageRepository extends JpaRepository<ThemeParkEntity, Long> {

}
