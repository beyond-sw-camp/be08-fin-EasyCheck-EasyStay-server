package com.beyond.easycheck.facilities.infrastructure.repository;

import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<FacilityEntity, Long> {
    @EntityGraph(attributePaths = {"accommodationEntity"})
    List<FacilityEntity> findAllByAccommodationEntity_Id(Long accommodationId);
}
