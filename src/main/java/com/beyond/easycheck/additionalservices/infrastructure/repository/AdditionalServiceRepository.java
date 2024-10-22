package com.beyond.easycheck.additionalservices.infrastructure.repository;

import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalServiceRepository extends JpaRepository<AdditionalServiceEntity, Long> {

    @EntityGraph(attributePaths = {"accommodationEntity"})
    List<AdditionalServiceEntity> findAllByAccommodationEntity_Id(Long accommodationId);
}
