package com.beyond.easycheck.accomodations.infrastructure.repository;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<AccommodationEntity, Long> {
}
