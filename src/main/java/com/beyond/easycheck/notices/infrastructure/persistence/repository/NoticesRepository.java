package com.beyond.easycheck.notices.infrastructure.persistence.repository;

import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NoticesRepository extends JpaRepository<NoticesEntity, Long> {

    @EntityGraph(attributePaths = {"userEntity"})
    List<NoticesEntity> findAllByAccommodationEntity_Id(Long accommodationId);
}
