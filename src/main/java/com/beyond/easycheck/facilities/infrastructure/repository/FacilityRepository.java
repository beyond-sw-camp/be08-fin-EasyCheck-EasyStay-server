package com.beyond.easycheck.facilities.infrastructure.repository;

import com.beyond.easycheck.facilities.infrastructure.entity.FacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<FacilityEntity, Long> {

}
