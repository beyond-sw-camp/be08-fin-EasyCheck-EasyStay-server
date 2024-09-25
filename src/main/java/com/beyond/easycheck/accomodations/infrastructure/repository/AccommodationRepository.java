package com.beyond.easycheck.accomodations.infrastructure.repository;

import com.beyond.easycheck.accomodations.infrastructure.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}
