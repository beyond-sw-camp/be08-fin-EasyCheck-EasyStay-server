package com.beyond.easycheck.additionalservices.infrastructure.repository;

import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalServiceRepository extends JpaRepository<AdditionalServiceEntity, Long> {

}
